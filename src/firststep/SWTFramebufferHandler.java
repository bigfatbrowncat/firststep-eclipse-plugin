package firststep;
public interface SWTFramebufferHandler {
	void handleException(Throwable t);
	void draw(SWTFramebuffer framebuffer) throws Exception;
	void frame();
}