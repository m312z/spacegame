package phys;

public class DefaultShapes {

	public static final Shape pointShape = new Shape(new Point2D[] {
			
	});
	public static final Shape basicSquare = new Shape(new Point2D[] {
			new Point2D(-1,-1),new Point2D(1,-1),new Point2D(1,1),new Point2D(-1,1)
	});;
	public static final Shape basicHex = new Shape(new Point2D[] {
			new Point2D((float)Math.cos(Math.toRadians(0)),(float)Math.sin(Math.toRadians(0))),
			new Point2D((float)Math.cos(Math.toRadians(60)),(float)Math.sin(Math.toRadians(60))),
			new Point2D((float)Math.cos(Math.toRadians(120)),(float)Math.sin(Math.toRadians(120))),
			new Point2D((float)Math.cos(Math.toRadians(180)),(float)Math.sin(Math.toRadians(180))),
			new Point2D((float)Math.cos(Math.toRadians(240)),(float)Math.sin(Math.toRadians(240))),
			new Point2D((float)Math.cos(Math.toRadians(300)),(float)Math.sin(Math.toRadians(300)))
	});;
	
}
