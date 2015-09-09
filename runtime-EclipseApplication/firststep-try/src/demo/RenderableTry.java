package demo;
import firststep.Color;
import firststep.Framebuffer;
import firststep.Renderable;

public class RenderableTry implements Renderable {

	@Override
	public void render(Framebuffer fb) {
		fb.beginDrawing();
		fb.beginPath();
		fb.circle(260, 210, 60);
		fb.fill( );
		
		fb.beginPath();
		fb.circle(150, 120, 50);
		fb.strokeColor(Color.fromRGBA(1, 0, 0.5f, 1));
		fb.stroke();
		
		fb.endDrawing();
	}

	@Override
	public int test(int i) {
		return i * 2;
	}

}
