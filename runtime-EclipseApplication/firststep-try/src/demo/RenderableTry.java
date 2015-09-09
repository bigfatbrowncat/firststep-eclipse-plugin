package demo;
import firststep.Color;
import firststep.Framebuffer;
import firststep.contracts.Renderable;

public class RenderableTry implements Renderable {

	@Override
	public void render(Framebuffer fb) {
		fb.beginDrawing();
		fb.beginPath();
		int d = 60;
		fb.circle(fb.getWidth() / 2, fb.getHeight() / 2, d);
		fb.fill( );
		
		fb.beginPath();
		fb.circle(150, 120, 50);
		fb.strokeColor(Color.fromRGBA(1, 0, 0.5f, 1));
		fb.stroke();
		
		fb.endDrawing();
	}


}
