package firststep;

public class MainFramebuffer extends Framebuffer {

	public MainFramebuffer(int width, int height) {
		super(width, height, 0);
	}

	public static void ensureNanoVGContextCreated() {
		Canvas.ensureNanoVGContextCreated();
	}
}
