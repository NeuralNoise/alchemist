package model.ES.processor.motion;

import com.simsilica.es.Entity;

import controller.ECS.LogicThread;
import controller.ECS.Processor;
import model.ES.component.motion.MotionCapacity;
import model.ES.component.motion.PlanarStance;
import model.ES.component.motion.PlanarVelocityToApply;
import model.ES.component.motion.physic.Physic;
import util.LogUtil;
import util.geometry.geom2d.Point2D;

public class VelocityApplicationProc extends Processor {
	@Override
	protected void registerSets() {
		register(PlanarStance.class, Physic.class, PlanarVelocityToApply.class, MotionCapacity.class);
	}
	
	@Override
	protected void onEntityEachTick(Entity e) {
		Physic ph = e.get(Physic.class);
		PlanarStance stance = e.get(PlanarStance.class);
		MotionCapacity capacity = e.get(MotionCapacity.class);

		Point2D velocityToApply = e.get(PlanarVelocityToApply.class).vector;
		velocityToApply = velocityToApply.getMult(1/ph.stat.mass);
		
		Point2D newVelocity = ph.velocity.getAddition(velocityToApply);
		//newVelocity = newVelocity.getTruncation(capacity.maxSpeed);
		Point2D newCoord = stance.coord.getAddition(newVelocity.getMult(LogicThread.TIME_PER_FRAME));

		setComp(e, new Physic(newVelocity, ph.stat, ph.spawnerException));
		setComp(e, new PlanarStance(newCoord, stance.orientation, stance.elevation, stance.upVector));
		setComp(e, new PlanarVelocityToApply(Point2D.ORIGIN));
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
