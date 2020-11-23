# Task 3

The scheduling simulator illustrates the behavior of scheduling 
algorithms against a simulated mix of process loads. The user can 
specify the number of processes, the mean and standard deviation 
for compute time and I/O blocking time for each process, and the 
duration of the simulation. At the end of the simulation a 
statistical summary is presented. Students may also be asked to 
write their own scheduling algorithms to be used with process 
loads defined by the instructor. 

### My changes and improvements

* **Implemented Fair-share scheduling**
* Moved code to the package `com.prenticehall.moss.sched`
* Got rid of useless `Common` utlities class
* Renamed `sProcess` to `Process`
* Use generics
* Better exception handling
* Added user ID parameter
* (TO-DO #1, #2) Renamed some parameters
* (TO-DO #3) Renamed `Run` method to `run`, added comments
* (TO-DO #4, #5) Added `block_time_average` and `block_time_stddev` parameters
* (TO-DO #6) Added quantum parameter
* Added quantum parameter for users as well
