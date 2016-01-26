package controller.regionPaging;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.jme3.app.Application;
import com.jme3.app.state.AppStateManager;
import com.jme3.scene.Node;
import com.simsilica.es.EntityData;

import app.AppFacade;
import controller.ECS.DataState;
import controller.builder.BuilderState;
import model.world.Region;
import model.world.RegionId;
import model.world.RegionLoader;
import model.world.WorldData;
import util.LogUtil;
import util.geometry.geom2d.Point2D;
import view.drawingProcessors.TerrainDrawer;

public class RegionPager extends BuilderState {
	
	private final RegionLoader loader = new RegionLoader();
	private EntityData entityData;
	private WorldData worldData;
	
	private final List<RegionId> neededRegions = new ArrayList<>();
	private final Map<RegionId, RegionCreator> creators = new HashMap<>();
	public final List<Region> builtRegions = new ArrayList<>();
	public final Map<Region, TerrainDrawer> drawers = new HashMap<>();
	
	private final Node worldNode;

	public RegionPager() {
		super("Region Builder", 20, 1);
		worldNode = new Node("World");
		AppFacade.getMainSceneNode().attachChild(worldNode);
	}
	
	@Override
	public void initialize(AppStateManager stateManager, Application app) {
		super.initialize(stateManager, app);
		worldData = stateManager.getState(DataState.class).getWorldData();
		entityData = stateManager.getState(DataState.class).getEntityData();
	}
	
	public void setNeededRegions(List<Point2D> newNeededRegions){
		// we start by transforming points in region Ids
		List<RegionId> ids = new ArrayList<>();
		for(Point2D p : newNeededRegions)
			ids.add(new RegionId(p));
		
		// we have to find the built regions that are not needed anymore and discard them
		List<RegionId> discardedRegions = new ArrayList<>(neededRegions);
		discardedRegions.removeAll(ids);
		for(RegionId id : discardedRegions){
			RegionId contained = null;
			for(RegionId oid : creators.keySet())
				if(oid.equals(id))
					contained = oid;
			if(contained != null){
				getBuilder().release(creators.get(contained));
				creators.remove(contained);
			}

			// this code doesn't work, don't know why
//			if(creators.get(id) != null){
//				getBuilder().release(creators.get(id));
//				creators.remove(id);
//			}
		}

		// and the new regions to build
		List<RegionId> missingRegions = new ArrayList<>(ids);
		missingRegions.removeAll(neededRegions);
		for(RegionId id : missingRegions){
			RegionCreator creator = getCreator(id);
			creators.put(id, creator);
			getBuilder().build(creator);
		}

		neededRegions.clear();
		neededRegions.addAll(ids);
	}
	
	public List<Region> getRegionsAtOnce(Point2D coord){
		coord = new Point2D((int)Math.floor(coord.x), (int)Math.floor(coord.y));
		List<Region> res = new ArrayList<>();
		res.add(loader.getRegion(coord));
		if(coord.x % Region.RESOLUTION == 0)
			res.add(loader.getRegion(coord.getAddition(-1, 0)));
		if(coord.y % Region.RESOLUTION == 0)
			res.add(loader.getRegion(coord.getAddition(0, -1)));
		if(coord.x % Region.RESOLUTION == 0 && coord.y % Region.RESOLUTION == 0)
			res.add(loader.getRegion(coord.getAddition(-1, -1)));
		for(Region r : res)
			if(!builtRegions.contains(r)){
				RegionId id = new RegionId(coord);
				builtRegions.add(r);
				RegionCreator creator = getCreator(id);
				creator.build();
				creator.apply(getBuilder());
				creators.put(id, creator);
			}
		return res; 
	}
	
	private RegionCreator getCreator(RegionId id){
		return new RegionCreator(id,
				loader,
				AppFacade.getStateManager().getState(DataState.class).getEntityData(),
				AppFacade.getStateManager().getState(DataState.class).getWorldData().getWorldEntity(),
				(region, drawer) -> {
					builtRegions.add(region);
					drawers.put(region, drawer);
					worldNode.attachChild(drawer.mainNode);
				},
				(region2) -> {
					LogUtil.info("drawers : " + + drawers.size());
					for(Region reg : drawers.keySet())
						LogUtil.info("    region " + reg.getId() + " (" + reg + ") drawer : " + drawers.get(reg));
							
					LogUtil.info("asked region " + region2.getId() + " (" + region2 + ") drawer : " + drawers.get(region2));
					builtRegions.remove(region2);
					worldNode.detachChild(drawers.get(region2).mainNode);
					drawers.remove(region2);
				}
				);
	}

}