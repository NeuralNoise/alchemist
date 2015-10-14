package view.drawingProcessors;

import java.util.HashMap;
import java.util.Map;

import model.ES.component.visuals.Model;
import view.SpatialPool;
import app.AppFacade;

import com.jme3.scene.Spatial;
import com.simsilica.es.Entity;

import controller.ECS.Processor;

public class ModelProc extends Processor {
	Map<String, Spatial> modelPrototypes = new HashMap<>();
	
	@Override
	protected void registerSets() {
		register(Model.class);
	}
	
	@Override
	protected void onEntityRemoved(Entity e) {
		AppFacade.getRootNode().detachChild(SpatialPool.models.remove(e.getId()));
	}

	@Override
	protected void onEntityEachTick(Entity e) {
		if(SpatialPool.models.containsKey(e.getId()))
			AppFacade.getRootNode().detachChild(SpatialPool.models.get(e.getId()));
		
		Model model = e.get(Model.class);
		Spatial s = getPrototype(model.path).clone();
		s.scale((float)model.scale);
		model.created = true;
		SpatialPool.models.put(e.getId(), s);
		AppFacade.getRootNode().attachChild(s);
	}
	
	private Spatial getPrototype(String modelPath){
		if (!modelPrototypes.containsKey(modelPath)) {
			Spatial s = AppFacade.getAssetManager().loadModel("models/" + modelPath);
			modelPrototypes.put(modelPath, s);
		}
		return modelPrototypes.get(modelPath);
	}
	
}
