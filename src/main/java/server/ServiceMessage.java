package server;

/**
 * Created by SurfinBirb on 22.04.2017.
 */
public class ServiceMessage {
    public String getProblem() {
        return problem;
    }

    public String problem;

    public ServiceMessage(String problem) {
        this.problem = problem;
    }
}
