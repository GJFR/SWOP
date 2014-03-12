package Test;

import Assembly.AssemblyTask;
import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.BeforeClass;
import org.junit.Test;

public class AssemblyTaskTest {
	
	private static AssemblyTask task;

	@BeforeClass
	public static void testCreate() {
		ArrayList<String> actions = new ArrayList<String>();
		actions.add("action1");
		actions.add("action2");
		actions.add("action3");
		actions.add("action4");
		String type = "type";
		task = new AssemblyTask(actions, type);
		assert(task.isCompleted());
		assertEquals(actions, task.getActions());
		assertEquals(type, task.getType());
	}
	
	@Test
	public void testCompleteTask() {
		task.completeTask();
		assert(task.isCompleted());
	}
	
	

}
