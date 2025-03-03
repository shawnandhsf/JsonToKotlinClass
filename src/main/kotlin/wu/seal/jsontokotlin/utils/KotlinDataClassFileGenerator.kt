package wu.seal.jsontokotlin.utils

import com.intellij.openapi.project.Project
import com.intellij.psi.PsiDirectory
import com.intellij.psi.PsiFileFactory
import wu.seal.jsontokotlin.classscodestruct.KotlinDataClass
import wu.seal.jsontokotlin.filetype.KotlinFileType
import wu.seal.jsontokotlin.getSplitClasses
import wu.seal.jsontokotlin.resolveInnerConflictClassName

class KotlinDataClassFileGenerator {

    fun generateSingleDataClassFile(
        packageDeclare: String,
        dataClasses: KotlinDataClass,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory
    ) {
        val fileNamesWithoutSuffix = currentDirExistsFileNamesWithoutKTSuffix(directory)
        var kotlinDataClassForGenerateFile = dataClasses
        while (fileNamesWithoutSuffix.contains(dataClasses.name)) {
            kotlinDataClassForGenerateFile =
                kotlinDataClassForGenerateFile.copy(name = kotlinDataClassForGenerateFile.name + "X")
        }
        generateKotlinDataClassFile(
            kotlinDataClassForGenerateFile.name,
            packageDeclare,
            kotlinDataClassForGenerateFile.getCode(),
            project,
            psiFileFactory,
            directory
        )
        val notifyMessage = "Kotlin Data Class file generated successful"
        showNotify(notifyMessage, project)
    }

    fun generateMultipleDataClassFiles(
        kotlinDataClass: KotlinDataClass,
        packageDeclare: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory
    ) {
        val fileNamesWithoutSuffix = currentDirExistsFileNamesWithoutKTSuffix(directory)
        val existsKotlinFileNames = IgnoreCaseStringSet().also { it.addAll(fileNamesWithoutSuffix) }
        val splitClasses = kotlinDataClass.resolveInnerConflictClassName(existsKotlinFileNames).getSplitClasses()
        val renameClassMap = getRenameClassMap(originNames = kotlinDataClass.getSplitClasses().map { it.name },
            currentNames = splitClasses.map { it.name })
        splitClasses.forEach { splitDataClass ->
            generateKotlinDataClassFile(
                splitDataClass.name,
                packageDeclare,
                splitDataClass.getCurrentClassCode(),
                project,
                psiFileFactory,
                directory
            )
        }
        val notifyMessage = buildString {
            append("${splitClasses.size} Kotlin Data Class files generated successful")
            if (renameClassMap.isNotEmpty()) {
                append("\n")
                append("These class names has been auto renamed to new names:\n ${renameClassMap.map { it.first + " -> " + it.second }.toList()}")
            }
        }
        showNotify(notifyMessage, project)
    }

    private fun currentDirExistsFileNamesWithoutKTSuffix(directory: PsiDirectory): List<String> {
        val kotlinFileSuffix = ".kt"
        return directory.files.filter { it.name.endsWith(kotlinFileSuffix) }
            .map { it.name.dropLast(kotlinFileSuffix.length) }
    }

    private fun getRenameClassMap(originNames: List<String>, currentNames: List<String>): List<Pair<String, String>> {
        if (originNames.size != currentNames.size) {
            throw IllegalArgumentException("two names list must have the same size!")
        }
        val renameMap = mutableListOf<Pair<String, String>>()
        originNames.forEachIndexed { index, originName ->
            if (originName != currentNames[index]) {
                renameMap.add(Pair(originName, currentNames[index]))
            }
        }
        return renameMap
    }

    private fun generateKotlinDataClassFile(
        fileName: String,
        packageDeclare: String,
        classCodeContent: String,
        project: Project?,
        psiFileFactory: PsiFileFactory,
        directory: PsiDirectory
    ) {
        val kotlinFileContent = buildString {
            if (packageDeclare.isNotEmpty()) {
                append(packageDeclare)
                append("\n\n")
            }
            val importClassDeclaration = ClassImportDeclaration.getImportClassDeclaration()
            if (importClassDeclaration.isNotBlank()) {
                append(importClassDeclaration)
                append("\n\n")
            }
            append(classCodeContent)
        }
        executeCouldRollBackAction(project) {
            val file =
                psiFileFactory.createFileFromText("${fileName.trim('`')}.kt", KotlinFileType(), kotlinFileContent)
            directory.add(file)
        }
    }
}
