# Turn DialogOS into a ROS node
Author: Timo Baumann

Implements a node in the robot operating system that allows to run and control DialogOS:

- select and start arbitrary dialog models
- abort an ongoing dialog

The node accepts the following commands on the `DialogOS_cmd` topic:
- `LOAD <FILE>` where FILE points to the dialog model to be loaded, relative to the node's working directory
- `START` starts the loaded dialog model
- `ABORT` aborts the ongoing dialog

The Java property "dialogos.showGUI" controls whether the system shows the GUI once a model is loaded.
The default is false (for server-based execution).

To try out this node, fire up roscore, set ROS_MASTER_URI and ROS_IP accordingly, 
call `./gradlew run` and eventually send the following via `rostopic`:
- `rostopic pub /DialogOS_cmd std_msgs/String "LOAD recursive.dos"`
- `rostopic pub /DialogOS_cmd std_msgs/String "START"`
- say something like "one plus two plus three" and hear "the result is six".
- you can now START again :-)
