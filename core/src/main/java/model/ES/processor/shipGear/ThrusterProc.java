package model.ES.processor.shipGear;

import com.simsilica.es.Entity;
import com.simsilica.es.EntityId;
import com.simsilica.es.EntitySet;

import controller.entityAppState.Processor;
import model.ModelManager;
import model.ES.component.command.PlanarNeededThrust;
import model.ES.component.motion.PlanarStance;
import model.ES.component.relation.BoneHolding;
import model.ES.component.relation.Parenting;
import model.ES.component.relation.PlanarHolding;
import model.ES.component.shipGear.Thruster;
import util.math.AngleUtil;
import util.math.Fraction;

public class ThrusterProc extends Processor {

	@Override
	protected void registerSets() {
		register(Thruster.class, Parenting.class);
	}

	@Override
	protected void onUpdated(float elapsedTime) {
        for(EntitySet set : sets)
        	for (Entity e : set){
        		manage(e, elapsedTime);
        	}
	}

	private void manage(Entity e, float elapsedTime) {
		Thruster thruster = e.get(Thruster.class);
		Parenting parenting = e.get(Parenting.class);
		EntityId holder = parenting.getParent();

		PlanarNeededThrust thrust = entityData.getComponent(holder, PlanarNeededThrust.class);
		PlanarStance stance = entityData.getComponent(holder, PlanarStance.class);
		double activationRate = 0;
		if(thrust != null && !thrust.getDirection().isOrigin()){
			double diff = AngleUtil.getSmallestDifference(thrust.getDirection().getAngle()-stance.orientation.getValue(), thruster.direction.get2D().getAngle());
			if(diff <= thruster.activationAngle.getValue()){
				activationRate = 1;
				if(!thruster.onOff)
					activationRate = 1-(diff/thruster.activationAngle.getValue());
			}
		}
		setComp(e, new Thruster(thruster.direction, thruster.activationAngle, new Fraction(activationRate), thruster.onOff));
	}

}
