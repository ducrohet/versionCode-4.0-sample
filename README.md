# versionCode-4.0-sample
 
This small sample shows the beginning of the new 
variant API in the android gradle plugin, and in particular
how to compute the version code and name via tasks.

Note that not all the API is available in 4.0, and it's all incubating.
This means the API may change before it goes stable

The custom plugin in `buildSrc` shows an example of using tasks to compute
the value, and store it in the task's file output.

The API can then be used to wire the output of these tasks to simple
Int and String properties, that can be consumed by any task.
