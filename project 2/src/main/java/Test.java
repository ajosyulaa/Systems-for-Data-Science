
import java.io.IOException;

public class Test {
	public static void main(String[] args) throws IOException {
		String cwd = System.getProperty("user.dir");
		System.out.println(cwd);
		ClassLoader loader = Test.class.getClassLoader();
        System.out.println(loader.getResource("Test.class"));
	}	
}

