package ru.zinoviewk.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.*
import org.jetbrains.uast.visitor.UastVisitor


private const val REPORT_MESSAGE = "Do not use it"

// Creating an issue
private val impl = Implementation(
    NotUsageJavaClassNameAsString::class.java,
    Scope.JAVA_FILE_SCOPE
)

private val issue = Issue.create(
    id = "BadMethodName",
    briefDescription = "Method name is not allowed",
    explanation = """This check ensures you call click listener that is throttled
            instead of a normal one which does not prevent double clicks.
    """.trimIndent(),
    category = Category.CORRECTNESS,
    priority = 10,
    severity = Severity.FATAL,
    implementation = impl
)


class NotUsageJavaClassNameAsString : Detector(), Detector.UastScanner {


    override fun getApplicableUastTypes(): List<Class<out UElement>> =
        listOf(UMethod::class.java)

    override fun createUastHandler(context: JavaContext): UElementHandler? {
        return Handler(context)
    }
}

class Handler(
    private val context: JavaContext
) : UElementHandler() {

    override fun visitMethod(node: UMethod) {
        if (node.name == "Daun") {
            context.report(
                issue,
                node,
                context.getLocation(node),
                REPORT_MESSAGE,
                createFix()
            )
        }
    }


    override fun visitParameter(node: UParameter) {

    }

    override fun visitCallExpression(node: UCallExpression) {
        super.visitCallExpression(node)
    }
   

    private fun createFix(): LintFix {
        return LintFix.create()
            .replace()
            .text("Daun")
            .with("Dolbaeb")
            .build()
    }

}

// Creating a registry

class ClassNameUsageRegistry : IssueRegistry() {

    override val issues: List<Issue> = listOf(
        issue
    )
}