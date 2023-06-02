package ru.zinoviewk.customview

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import ru.zinoviewk.customview.Graph.*
import kotlin.random.Random

fun Any.log(message: String) {
    Log.d("zinoviewkska", message)
}

class MainActivity : AppCompatActivity() {


    override fun onCreate(bundle: Bundle?) {
        super.onCreate(bundle)
        setContentView(R.layout.activity_main)

        val graphView = findViewById<Graph>(R.id.graph)

        graphView.setGraph(generateGraph())
        graphView.findShortestPath("A")

        findViewById<View>(R.id.next_btn).setOnClickListener {
            graphView.drawNextStepToFindShortestPath()
        }

        findViewById<View>(R.id.prev_btn).setOnClickListener {
            graphView.drawPrevStepToFindShortestPath()
        }
    }


    private fun generateGraph() : Map<String, List<GraphNode>> {
        val graph = mutableMapOf<String, MutableList<GraphNode>>()
        val pullOfNodes = mutableListOf<String>()
        val possibleNeighbours = mutableMapOf<String, MutableList<String>>()

        for (i in 65..70) {
            val currNodeId = i.toChar().toString()
            pullOfNodes.add(currNodeId)
            possibleNeighbours[currNodeId] = mutableListOf()
            graph[currNodeId] = mutableListOf()
            for (j in i + 1..70) {
                val idOfPossibleNeighbour = j.toChar().toString()
                possibleNeighbours[currNodeId]?.add(idOfPossibleNeighbour)
            }
        }

        val seen = mutableSetOf<String>()
        pullOfNodes.forEach { nodeId ->
            if(nodeId != pullOfNodes.last())  {
                val neighbours = possibleNeighbours[nodeId]!!
                val neighboursCount = Random.nextInt(1, neighbours.size + 1)
                var currNeighbourIndex = 0
                while (currNeighbourIndex < neighboursCount) {
                    val randomNeighbourIndex = Random.nextInt(0, neighbours.size)
                    val neighbourId = neighbours[randomNeighbourIndex]

                    if (nodeId != neighbourId && seen.add("$nodeId->$neighbourId")) {
                        val weigh = Random.nextInt(1, 10 + 1)
                        graph[nodeId]?.add(GraphNode(id = neighbourId, weigh = weigh))
                        currNeighbourIndex++;
                    }
                }
            }
        }

        return graph
    }

}
