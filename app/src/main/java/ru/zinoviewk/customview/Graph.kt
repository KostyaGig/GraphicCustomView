package ru.zinoviewk.customview

import android.app.AliasActivity
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import kotlin.random.Random

private const val GRAPH_BOTTOM_MARGIN = 50

class Graph @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var graph = mapOf<String, List<GraphNode>>()
    private val viewGraph = mutableMapOf<String, MutableList<ViewGraphNode>>()
    private val nodeIdToViewGraphNode = mutableMapOf<String, ViewGraphNode>()

    private val nodePaint = Paint().apply {
        this.color = Color.RED
        this.style = Paint.Style.FILL
        this.flags = Paint.ANTI_ALIAS_FLAG
    }

    private val adjacencyPaint = Paint().apply {
        this.color = Color.WHITE
        this.flags = Paint.ANTI_ALIAS_FLAG
    }

    private val shortestPathPaint = Paint().apply {
        this.color = Color.BLUE
        this.flags = Paint.ANTI_ALIAS_FLAG
    }

    private val nodeIdPaint = Paint().apply {
        this.color = Color.WHITE
        this.textSize = 12f
        this.flags = Paint.ANTI_ALIAS_FLAG
    }

    private val connectionWeighPaint = Paint().apply {
        this.color = Color.WHITE
        this.textSize = 15f
        this.flags = Paint.ANTI_ALIAS_FLAG
    }

    private val nodeIdTextBounds = Rect()

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val desiredWidth = 200
        val desiredHeight = 200

        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)

        val width: Int = when (widthMode) {
            MeasureSpec.EXACTLY -> {
                widthSize
            }
            MeasureSpec.AT_MOST -> {
                desiredWidth.coerceAtMost(widthSize)
            }
            else -> {
                desiredWidth
            }
        }

        //Measure Height

        //Measure Height
        val height: Int = when (heightMode) {
            MeasureSpec.EXACTLY -> {
                heightSize
            }
            MeasureSpec.AT_MOST -> {
                desiredHeight.coerceAtMost(heightSize)
            }
            else -> {
                desiredHeight
            }
        }

        //MUST CALL THIS

        //MUST CALL THIS
        setMeasuredDimension(width, height)
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)

        canvas?.let(::drawGraphNodes)
        canvas?.let(::drawAdjacency)
        canvas?.let(::drawShortestPathSteps)
    }

    private var isDragging = false
    private var draggingViewGraphNode: ViewGraphNode? = null
    override fun onTouchEvent(event: MotionEvent?): Boolean {
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                // Check if the touch event is within the view's bounds
                if (x >= 0 && x <= width && y >= 0 && y <= height - GRAPH_BOTTOM_MARGIN) {
                    isDragging = true
                }
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                if (event.x >= 0 && event.x <= width && event.y >= 0 && event.y <= height - GRAPH_BOTTOM_MARGIN) {
                    if (isDragging) {
                        if (draggingViewGraphNode == null)
                            draggingViewGraphNode = getDraggingGraphNodeId(event.x, event.y)
                        else {
                            if (!draggingViewGraphNode!!.isEmpty()) {
                                draggingViewGraphNode?.x = event.x - draggingViewGraphNode!!.radius
                                draggingViewGraphNode?.y = event.y - draggingViewGraphNode!!.radius
                                invalidate()
                            }
                        }
                    }
                }
            }
            MotionEvent.ACTION_UP -> {
                draggingViewGraphNode = null
                isDragging = false
            }
        }
        return super.onTouchEvent(event)
    }

    private fun getDraggingGraphNodeId(x: Float, y: Float): ViewGraphNode {
        nodeIdToViewGraphNode.map { it.key }.forEach { nodeId ->
            val node = nodeIdToViewGraphNode[nodeId]!!
            if (isDraggingInsideGraphNode(node, x, y)) {
                return node
            }
        }
        return ViewGraphNode()
    }

    private fun isDraggingInsideGraphNode(
        node: ViewGraphNode,
        x: Float,
        y: Float
    ): Boolean {
        return x >= node.x && x <= node.x + 2 * node.radius
                && y >= node.y && y <= node.y + 2 * node.radius
    }


    private fun drawGraphNodes(canvas: Canvas) {
        //val nodes = viewGraph.entries.map { it.key }
        nodeIdToViewGraphNode.forEach { entry ->
            val node = entry.value
            nodeIdPaint.getTextBounds(node.id, 0, node.id.length, nodeIdTextBounds)
            canvas.drawCircle(node.x + node.radius, node.y + node.radius, node.radius, nodePaint)

            canvas.drawText(
                node.id,
                node.x + node.radius - nodeIdTextBounds.width() / 2,
                node.y + node.radius + nodeIdTextBounds.height() / 2,
                nodeIdPaint
            )
        }
    }

    private fun drawAdjacency(canvas: Canvas) {
        //val nodes = viewGraph.entries.map { it.key }
        nodeIdToViewGraphNode.forEach { entry ->
            val node = entry.value
            val neighbours = viewGraph[node.id]
            neighbours?.forEach { neighbour ->
                drawConnectionBetweenNodes(
                    canvas,
                    node,
                    neighbour,
                    adjacencyPaint
                )
                drawWeigh(
                    canvas,
                    node,
                    neighbour
                )
            }
        }
    }

    private fun drawWeigh(
        canvas: Canvas,
        node1: ViewGraphNode,
        node2: ViewGraphNode
    ) {

        val midX = (node1.x + node2.x) / 2
        val midY = (node1.y + node2.y) / 2

        canvas.drawText(
            node2.weigh.toString(),
            midX,
            midY,
            connectionWeighPaint,
        )
    }


    private fun drawShortestPathSteps(canvas: Canvas) {
        val shortestPathStep = stepsToFindShortestPath[currStepToFindShortestPath]
        val steps = shortestPathStep.currPath
        for (i in 0 until steps.size - 1) {
            val currNodeId = steps[i]
            val nextNodeId = steps[i + 1]
            val curr = nodeIdToViewGraphNode[currNodeId]!!
            val next = nodeIdToViewGraphNode[nextNodeId]!!

            drawConnectionBetweenNodes(
                canvas,
                curr,
                next,
                shortestPathPaint
            )
        }

        drawStepsAsText(canvas)
    }

    private fun drawStepsAsText(canvas: Canvas) {
        val currShortestStep = stepsToFindShortestPath[currStepToFindShortestPath]
        val (steps, weigh) = currShortestStep.currPath to currShortestStep.weigh

        // todo move
        val marginFromLeft = 10
        val marginFromBottom = 10

        val x0 = 0 + marginFromLeft.toFloat()
        val y0 = height - marginFromBottom.toFloat()

        val pathAsString = java.lang.StringBuilder()

        var drawFirstVertex = false
        for (i in 0 until steps.size - 1) {
            val currNodeId = steps[i]
            val nextNodeId = steps[i + 1]

            if(!drawFirstVertex) {
                drawFirstVertex = true
                pathAsString.append("$currNodeId -> $nextNodeId")
            } else {
                pathAsString.append(" -> $nextNodeId")
            }
        }
        if(currShortestStep.currPath.size > 1) pathAsString.append(" = $weigh")

        val text = pathAsString.toString()
        canvas.drawText(
            text,
            x0,
            y0,
            nodeIdPaint
        )
    }

    private fun drawConnectionBetweenNodes(
        canvas: Canvas,
        node1: ViewGraphNode,
        node2: ViewGraphNode,
        paint: Paint
    ) {
        canvas.drawLine(
            node1.x + node1.radius,
            node1.y + node1.radius,
            node2.x + node2.radius,
            node2.y + node2.radius,
            paint
        )
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(w, h, oldw, oldh)
        setUpGraph()
    }

    private fun setUpGraph() {
        val nodes = graph.entries.iterator()
        while (nodes.hasNext()) {
            val x = Random.nextInt(1, 200).toFloat()
            val y = Random.nextInt(1, 200).toFloat()
            val radius = 10.toFloat()

            if (nodeCanBeAdded(x.toInt(), y.toInt(), radius.toInt())) {
                val nodeId = nodes.next().key
                val viewGraphNode = ViewGraphNode(nodeId, x, y, radius, Int.MAX_VALUE)
                viewGraph[nodeId] = mutableListOf()
                nodeIdToViewGraphNode[nodeId] = viewGraphNode
            }

        }
        setUpAdjacency()
    }

    private fun setUpAdjacency() {
        val nodesFromGraph = graph.entries.map { it.key }
        nodesFromGraph.forEach { nodeId ->
            val neighbours = graph[nodeId]!!
            neighbours.forEach { neighbourNode ->

                // val viewGraphNode = nodeIdToViewGraphNode[nodeId]
                val neighbourGraphNode =
                    nodeIdToViewGraphNode[neighbourNode.id]!!.also {
                        it.weigh = neighbourNode.weigh
                    }
                viewGraph[nodeId]?.add(neighbourGraphNode)
            }
        }
    }

    private val coordinatesOfAddedNodes = mutableListOf<Triple<Int, Int, Int>>()
    private fun nodeCanBeAdded(node1X: Int, node1Y: Int, node1Radius: Int): Boolean {
        if (!onTheScreen(node1X, node1Y, node1Radius)) return false

        coordinatesOfAddedNodes.forEach { (node2X, node2Y, node2Radius) ->
            val dx = node1X - node2X
            val dy = node1Y - node2Y
            val dr = node1Radius - node2Radius
            val sr = node1Radius + node2Radius

            val sum = dx * dx + dy * dy
            if (sum >= dr * dr && sum <= sr * sr) return false
        }

        coordinatesOfAddedNodes.add(
            Triple(
                node1X, node1Y, node1Radius
            )
        )
        return true
    }


    private fun onTheScreen(nodeX: Int, nodeY: Int, nodeRadius: Int): Boolean {
        return nodeX - nodeRadius >= 0 && nodeX + nodeRadius < width
                && nodeY - nodeRadius >= 0 && nodeY + nodeRadius <= height - GRAPH_BOTTOM_MARGIN
    }

    fun setGraph(graph: Map<String, List<GraphNode>>) {
        this.graph = graph
    }

    private var minWeight = Int.MAX_VALUE
    private val shortestPath = mutableListOf<String>()

    private var currStepToFindShortestPath = 0
    private val stepsToFindShortestPath = mutableListOf<ShortestPathStep>()

    fun findShortestPath(from: String) {
        findShortestPath(from, graph, listOf(), 0)
    }

    private fun findShortestPath(
        from: String,
        graph: Map<String, List<GraphNode>>,
        currPath: List<String>,
        currWeigh: Int
    ) {
        val vertex = graph[from] ?: emptyList()

        val path = mutableListOf<String>().apply {
            addAll(currPath)
            add(from)
        }

        stepsToFindShortestPath.add(
            ShortestPathStep(
                path,
                currWeigh
            )
        )

        log(stepsToFindShortestPath.toString())

        if (vertex.isEmpty()) {
            if (minWeight > currWeigh) {
                minWeight = currWeigh
                shortestPath.also {
                    it.clear()
                    it.addAll(path)
                }
            }
            return
        }

        vertex.forEach {
            val nodeWithMinWeigh = it
            findShortestPath(
                nodeWithMinWeigh.id,
                graph,
                path,
                currWeigh + nodeWithMinWeigh.weigh
            )
        }
    }

    fun drawNextStepToFindShortestPath() {
        if (currStepToFindShortestPath + 1 < stepsToFindShortestPath.size)
            currStepToFindShortestPath++
        invalidate()
    }

    fun drawPrevStepToFindShortestPath() {
        if (currStepToFindShortestPath - 1 >= 0)
            currStepToFindShortestPath--
        invalidate()
    }

    private class ViewGraphNode(
        val id: String = "",
        var x: Float = -1f,
        var y: Float = -1f,
        val radius: Float = -1f,
        var weigh: Int = -1
    ) {
        override fun toString(): String {
            return "$id - $x : $y : $radius, weigh $weigh"
        }

        override fun equals(other: Any?): Boolean {
            return other is ViewGraphNode && other.id == id
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }

        fun isEmpty() = id.isEmpty()
    }

    class GraphNode(
        val id: String = "",
        val weigh: Int = Int.MIN_VALUE
    ) {
        override fun toString(): String {
            return "$id : $weigh"
        }

        override fun equals(other: Any?): Boolean {
            return other is GraphNode && other.id == id
        }

        override fun hashCode(): Int {
            return super.hashCode()
        }
    }

    private class ShortestPathStep(
        val currPath: List<String>,
        val weigh: Int
    ) {
        override fun toString(): String {
            return "$weigh, $currPath"
        }
    }

}