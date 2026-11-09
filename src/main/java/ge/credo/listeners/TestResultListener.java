package ge.credo.listeners;

import ge.credo.database.TestResultRepository;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;


public final class TestResultListener implements ITestListener {
    private final Map<String, Boolean> aggregateResults = new ConcurrentHashMap<>();
    private TestResultRepository repository;

    @Override
    public void onStart(ITestContext context) {
        aggregateResults.clear();
        repository = new TestResultRepository();
        repository.initialize();
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        record(result, true);
    }

    @Override
    public void onTestFailure(ITestResult result) {
        record(result, false);
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        record(result, false);
    }

    private void record(ITestResult result, boolean passed) {
        String name = result.getMethod().getMethodName();
        aggregateResults.merge(name, passed, (previous, current) -> previous && current);
    }

    @Override
    public void onFinish(ITestContext context) {
        aggregateResults.forEach((method, passed) ->
                repository.saveResult(method, passed ? "PASSED" : "FAILED"));
    }
}
