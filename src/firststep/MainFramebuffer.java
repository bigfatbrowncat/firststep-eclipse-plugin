package firststep;

public class MainFramebuffer extends Framebuffer {

	public MainFramebuffer(int width, int height) {
		super(width, height, 0);
	}

	public static void ensureNanoVGContextCreated() {
		Canvas.ensureNanoVGContextCreated();
	}
	
	@Override
	public void clearDrawingStack() {
		super.clearDrawingStack();
	}
	
	@Override
	public void checkStackClear() {
		super.checkStackClear();
	}
}
