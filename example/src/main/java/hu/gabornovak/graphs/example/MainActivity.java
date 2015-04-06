package hu.gabornovak.graphs.example;

import android.app.Activity;
import android.content.Context;
import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.WindowManager;

import java.util.Random;

import hu.gabornovak.graphs.data.Graph;
import hu.gabornovak.graphs.data.Node;
import hu.gabornovak.graphs.view.GraphView;


public class MainActivity extends Activity {
    private static final int INITIAL_MARGIN = 200;

    private Random random = new Random();

    private int screenWidth;
    private int screenHeight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display display = wm.getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);

        screenWidth = size.x;
        screenHeight = size.y;

        GraphView graphView = (GraphView) findViewById(R.id.graph);

        Graph graph = new Graph();
        Point p = generateRandomPointOnScreen();

        Node n1 = graph.createNewNode(new ExampleNodeView(this));
        n1.setWeight(0.9f);
        n1.setPosition(p.x, p.y);

        p = generateRandomPointOnScreen();
        Node n2 = graph.createNewNode(new ExampleNodeView(this));
        n2.setWeight(0.5f);
        n2.setPosition(p.x, p.y);

        p = generateRandomPointOnScreen();
        Node n3 = graph.createNewNode(new ExampleNodeView(this));
        n3.setWeight(0.96f);
        n3.setPosition(p.x, p.y);

        p = generateRandomPointOnScreen();
        Node n4 = graph.createNewNode(new ExampleNodeView(this));
        n4.setWeight(0.9f);
        n4.setPosition(p.x, p.y);

        p = generateRandomPointOnScreen();
        Node n5 = graph.createNewNode(new ExampleNodeView(this));
        n5.setWeight(0.5f);
        n5.setPosition(p.x, p.y);

        ExampleNodeView nodeView = new ExampleNodeView(this);
        Node n6 = graph.createNewNode(nodeView);
        n6.setFixed(true);
        nodeView.setFixed(true);
        n6.setWeight(0.96f);
        n6.setPosition(500, 500);

        graph.createNewConnection(n1, n5).setLabel("42");
        graph.createNewConnection(n2, n3);
        graph.createNewConnection(n3, n4);
        graph.createNewConnection(n2, n5);
        graph.createNewConnection(n1, n6);

        graphView.setGraph(graph, new ExampleConnectionDrawer());
    }

    private Point generateRandomPointOnScreen() {
        return new Point(random.nextInt(screenWidth - INITIAL_MARGIN * 2) + INITIAL_MARGIN,
                random.nextInt(screenHeight - INITIAL_MARGIN * 2) + INITIAL_MARGIN);
    }
}
