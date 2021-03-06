package plugin.infiniteWorld.world.terrain.atlas;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Stores and manage layers of texture to paint on the ground. Atlas itself doesn't know the textures, and provides only alpha channels used by the view to draw
 * and blend textures on a multiple material. This class contains also methods for serialization/deserialization by Byte, has the data may be huge in a more
 * common XML format.
 */
public class Atlas {
	private static final int LAYER_COUNT = 8;
	public static final int RESOLUTION_RATIO = 2;

	private final int width, height;
	private final List<AtlasLayer> layers = new ArrayList<>();
	private final List<ByteBuffer> buffers = new ArrayList<>();

	private boolean toUpdate = false;

	public Atlas(@JsonProperty("width")int width,
			@JsonProperty("height")int height,
			@JsonProperty("flatData")String flatData) {
		this.width = width;
		this.height = height;
		int index = 0;
		for (int i = 0; i < LAYER_COUNT; i++) {
			AtlasLayer l = new AtlasLayer(width, height);
			for (int xy = 0; xy < width * height; xy++) {
				if(flatData.charAt(index) == '-'){
					int nbZero = getByteFromHexString(flatData.substring(index+1, index+3)) & 0xFF;
					while(nbZero-- > 0)
						l.setByte(xy++, Byte.MIN_VALUE);
					xy--;
					index += 4;
				} else if(flatData.charAt(index) == '+'){
					int nbMax = getByteFromHexString(flatData.substring(index+1, index+3)) & 0xFF;
					while(nbMax-- > 0)
						l.setByte(xy++, Byte.MAX_VALUE);
					xy--;
					index += 4;
				} else {
					byte b = getByteFromHexString(flatData.substring(index, index+2));
					l.setByte(xy, b);
					index += 3;
				}
			}
			layers.add(l);
		}
		buffers.add(buildBuffer(0));
		buffers.add(buildBuffer(1));
		toUpdate = true;
	}
	
	public Atlas(int mapWidth, int mapHeight) {
		width = mapWidth * RESOLUTION_RATIO;
		height = mapHeight * RESOLUTION_RATIO;
		layers.add(new AtlasLayer(width, height, AtlasLayer.MAX_VALUE));
		for (int i = 1; i < LAYER_COUNT; i++) {
			layers.add(new AtlasLayer(width, height, 0));
		}
		buffers.add(buildBuffer(0));
		buffers.add(buildBuffer(1));
	}

	private ByteBuffer buildBuffer(int index) {
		ByteBuffer res = ByteBuffer.allocateDirect(width * height * 4);
		int firstLayerIndex = index * 4;
		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				res.asIntBuffer().put(y * width + x, getBufferVal(x, y, firstLayerIndex));
			}
		}
		return res;
	}

	public ByteBuffer getBuffer(int index) {
		return buffers.get(index);
	}

	public String getFlatData(){
		StringBuilder sb = new StringBuilder(height*width*2*LAYER_COUNT); 
		for (AtlasLayer l : layers) {
			for(int i = 0; i < l.getBytes().size(); i++){
				byte b = (l.getBytes().get(i));
				if(b == Byte.MIN_VALUE){
					// grouping of zero values
					short nbZero = 1;
					while(i+nbZero < l.getBytes().size() &&
							nbZero < 250 &&
							(l.getBytes().get(i+nbZero)) == Byte.MIN_VALUE){
						nbZero++;
					}
					if(nbZero > 4){
						sb.append("-"+String.format("%02X,", nbZero));
						i = i+nbZero-1;
						continue;
					}
				} else if(b == Byte.MAX_VALUE){
					// grouping of max (255) values
					short nb255 = 1;
					while(i+nb255 < l.getBytes().size() &&
							nb255 < 250 &&
							(l.getBytes().get(i+nb255)) == Byte.MAX_VALUE){
						nb255++;
					}
					if(nb255 > 4){
						sb.append("+"+String.format("%02X,", nb255));
						i = i+nb255-1;
						continue;
					}
					
				}
				sb.append(String.format("%02X,", b));
			}
		}
		return sb.toString();
	}
	
	public void updatePixel(int x, int y) {
		for (int i = 0; i < buffers.size(); i++) {
			int firstLayerIndex = i * 4;
			buffers.get(i).asIntBuffer().put(y * width + x, getBufferVal(x, y, firstLayerIndex));
		}
		toUpdate = true;
	}

	private int getBufferVal(int x, int y, int firstLayerIndex) {
		int r = (int) Math.round(layers.get(firstLayerIndex).get(x, y)) << 24;
		int g = (int) Math.round(layers.get(firstLayerIndex + 1).get(x, y)) << 16;
		int b = (int) Math.round(layers.get(firstLayerIndex + 2).get(x, y)) << 8;
		int a = (int) Math.round(layers.get(firstLayerIndex + 3).get(x, y));
		return (r + g + b + a);
	}

	@JsonIgnore
	public List<AtlasLayer> getLayers() {
		return layers;
	}

	@JsonIgnore
	public boolean isToUpdate() {
		return toUpdate;
	}

	@JsonIgnore
	public void setToUpdate(boolean toUpdate) {
		this.toUpdate = toUpdate;
	}

	public int getWidth() {
		return width;
	}

	public int getHeight() {
		return height;
	}
	
	public static byte getByteFromHexString(String s) {
	    return (byte)((Character.digit(s.charAt(0), 16) << 4) + Character.digit(s.charAt(1), 16));
	}

}
