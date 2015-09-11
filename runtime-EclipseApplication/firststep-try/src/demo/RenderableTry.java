package demo;
import firststep.Color;
import firststep.Framebuffer;
import firststep.contracts.Renderable;

public class RenderableTry implements Renderable {

	@Override
	public void render(Framebuffer fb) {
		fb.beginPath();
		int d = 80;
		fb.circle(fb.getWidth() / 2, (int)(fb.getHeight() / 2.5), d);
		fb.fill( );
		
		fb.beginPath();
		fb.circle(150, 120, d * .8f);
		fb.strokeColor(Color.fromRGBA(1, 0, 0.5f, 1));
		fb.stroke();
	}


}
