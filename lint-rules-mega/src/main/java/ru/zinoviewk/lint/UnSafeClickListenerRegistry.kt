package ru.zinoviewk.lint

import com.android.tools.lint.client.api.IssueRegistry
import com.android.tools.lint.client.api.UElementHandler
import com.android.tools.lint.detector.api.*
import org.jetbrains.uast.UCallExpression
import org.jetbrains.uast.UElement

private const val REPORT_MESSAGE = "Use setThrottlingClickListener"

// Creating an issue
private val correctClickListenerImplementation = Implementation(
    CorrectClickListenerDetector::class.java,
    Scope.JAVA_FILE_SCOPE
)

private val unsafeClickListenerIssue = Issue.create(
    id = "UnsafeClickListener",
    briefDescription = "Unsafe click listener",
    explanation =  """This check ensures you call click listener that is throttled
            instead of a normal one which does not prevent double clicks.
    """.trimIndent(),
    category = Category.CORRECTNESS,
    priority = 6,
    severity = Severity.WARNING,
    implementation = correctClickListenerImplementation
)

// Creating a detector

class CorrectClickListenerDetector : Detector(), Detector.UastScanner {


    override fun getApplicableUastTypes(): List<Class<out UElement>> {
        return listOf<Class<out UElement>>(UCallExpression::class.java)
    }

    override fun createUastHandler(context: JavaContext): UElementHandler {
        return object : UElementHandler() {
            override fun visitCallExpression(node: UCallExpression) {
                if(node.methodName != null && node.methodName.equals("setOnClickListener", ignoreCase = true)) {
                    context.report(
                        unsafeClickListenerIssue,
                        node,
                        context.getLocation(node),
                        REPORT_MESSAGE,
                        createFix()
                    )
                }
            }
        }
    }

    private fun createFix() : LintFix {
        return fix()
            .replace()
            .text("setOnClickListener")
            .with("setThrottlingClickListener")
            .build()
    }
}

// Creating a registry

class UnSafeClickListenerRegistry : IssueRegistry() {

    override val issues: List<Issue> = listOf(
        unsafeClickListenerIssue
    )
}