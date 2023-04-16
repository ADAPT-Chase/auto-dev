package cc.unitmesh.devti.runconfig.ui

import cc.unitmesh.devti.ai.OpenAI.DEFAULT_OPEN_AI_MAX_TOKENS
import cc.unitmesh.devti.ai.OpenAIVersion
import cc.unitmesh.devti.runconfig.DtRunConfiguration
import com.intellij.openapi.diagnostic.Logger
import com.intellij.openapi.options.SettingsEditor
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.ui.dsl.builder.Cell
import com.intellij.ui.dsl.builder.Row
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import javax.swing.JComponent

class DtSettingsEditor(project: Project) : SettingsEditor<DtRunConfiguration>() {
    private var completionProvider = DtCommandCompletionProvider()
    private var openAiMaxTokens: Int = DEFAULT_OPEN_AI_MAX_TOKENS

    private var panel: JComponent? = null

    private val githubInput = DtCommandLineEditor(project, completionProvider)
    private val aiApiToken = DtCommandLineEditor(project, completionProvider)
    private var engineVersion = ComboBox<OpenAIVersion>().apply {
        OpenAIVersion.values()
            .sortedBy { it.index }
            .forEach { addItem(it) }
    }
    private val maxTokens = DtCommandLineEditor(project, completionProvider).apply {
        text = openAiMaxTokens.toString()
    }

    override fun createEditor(): JComponent = panel {
        row("Github Token:") {
            fullWidthCell(githubInput)
        }

        row("API Engine:") {
            comboBox(listOf("OpenAI"))
        }

        row("AI API Key:") {
            fullWidthCell(aiApiToken)
        }

        row("API Engine:") {
            cell(engineVersion)
        }

        row("Max Tokens") {
            fullWidthCell(maxTokens)
        }
    }.also {
        panel = it
    }

    override fun resetEditorFrom(configuration: DtRunConfiguration) {
        githubInput.text = configuration.options.githubToken()
        aiApiToken.text = configuration.options.openAiApiKey()
        engineVersion.selectedIndex = configuration.options.aiVersion()
        openAiMaxTokens = configuration.options.aiMaxTokens()
    }

    override fun applyEditorTo(configuration: DtRunConfiguration) {
        configuration.setGithubToken(githubInput.text)
        configuration.setOpenAiApiKey(aiApiToken.text)
        configuration.setAiVersion(OpenAIVersion.fromIndex(engineVersion.selectedIndex))
        configuration.setAiMaxTokens(openAiMaxTokens)
    }

    companion object {
        val logger = Logger.getInstance(DtSettingsEditor::class.java)
    }
}

fun <T : JComponent> Row.fullWidthCell(component: T): Cell<T> {
    return cell(component)
        .horizontalAlign(HorizontalAlign.FILL)
}
