package app.dialogos.ros;

import org.ros.namespace.GraphName;
import org.ros.node.ConnectedNode;
import org.ros.node.Node;
import org.ros.node.NodeMain;
import org.ros.node.topic.Subscriber;

/**
 * A node in the robot operating system that allows to control DialogOS:
 * - select and start arbitrary dialog models
 * - abort an ongoing dialog
 *
 * The node accepts the following commands on the "DialogOS_cmd" topic:
 * "LOAD <FILE>" where FILE points to the dialog model to be loaded, relative to the node's working directory
 * "START" starts the loaded dialog model
 * "ABORT" aborts the ongoing dialog
 *
 * you can only start if a dialog has been loaded (no need to re-load after the dialog is finished)
 * you can only start if the dialog is not running
 * you can abort if the dialog is running or has finished on its own
 *
 * The Java property "dialogos.showGUI" controls whether the system shows the GUI once a model is loaded.
 * The default is false (for server-based execution).
 *
 */
public class DialogOSNode implements NodeMain {

    DialogOSAdapter adapter;

    public DialogOSNode() {
        adapter = new DialogOSAdapter();
    }

    @Override public GraphName getDefaultNodeName() {
        return GraphName.of("DialogOS_Control");
    }

    @Override public void onStart(final ConnectedNode connectedNode) {
        final Subscriber<std_msgs.String> subscriber = connectedNode.newSubscriber("DialogOS_cmd", std_msgs.String._TYPE);
        subscriber.addMessageListener(message -> {
            String cmd = message.getData();
            System.err.println(cmd);
            if (cmd.startsWith("LOAD ")) {
                String argument = cmd.replaceAll("^LOAD ", "");
                adapter.loadDialog(argument);
            } else if (cmd.equals("START")) {
                adapter.startDialog();
            } else if (cmd.equals("ABORT")) {
                adapter.abortDialog();
            } else {
                throw new IllegalArgumentException("illegal command: " + cmd);
            }
        });
    }

    @Override public void onShutdown(Node node) {
        adapter.abortDialog();
    }

    @Override
    public void onShutdownComplete(Node node) {

    }

    @Override
    public void onError(Node node, Throwable throwable) {
        adapter.abortDialog();
    }

}
