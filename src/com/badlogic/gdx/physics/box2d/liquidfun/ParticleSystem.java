
package com.badlogic.gdx.physics.box2d.liquidfun;

import java.util.ArrayList;

import org.andengine.extension.physics.box2d.PhysicsWorld;
import org.andengine.util.adt.color.Color;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Shape;
import com.badlogic.gdx.physics.box2d.Transform;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.LongMap;

/** Manages all particles; read http://google.github.io/liquidfun/ for more information
 * @author FinnStr */
public class ParticleSystem {
	// @off
	/*JNI
#include <Box2D/Box2D.h>
	*/
	
	private final long addr;
	private final World mWorld;
	
	/** all known particleGroups **/
	protected final LongMap<ParticleGroup> particleGroups = new LongMap<ParticleGroup>(100);
	
	public ParticleSystem(PhysicsWorld pWorld, ParticleSystemDef pDef) {
		this(pWorld.getWorld(), pDef);
	}
	
	public ParticleSystem(World pWorld, ParticleSystemDef pDef) {	
		long worldAddr = pWorld.getAddress();
		mWorld = pWorld;
		
		addr = jniCreateParticleSystem(worldAddr, pDef.radius, pDef.pressureStrength, 
			pDef.dampingStrength, pDef.elasticStrength, pDef.springStrength, pDef.viscousStrength,
			pDef.surfaceTensionPressureStrength, pDef.surfaceTensionNormalStrength, pDef.repulsiveStrength,
			pDef.powderStrength, pDef.ejectionStrength, pDef.staticPressureStrength, 
			pDef.staticPressureRelaxation, pDef.staticPressureIterations, pDef.colorMixingStrength, pDef.destroyByAge,
			pDef.lifetimeGranularity);

		setParticleDensity(pDef.density);
		mWorld.particleSystems.put(addr, this);
	}
	
	private native long jniCreateParticleSystem(long worldAddr, float radius, float pressureStrength, 
		float dampingStrength, float elasticStrength, float springStrength, float viscousStrength,
		float surfaceTensionPressureStrength, float surfaceTensionNormalStrength, float repulsiveStrength,
		float powderStrength, float ejectionStrength, float staticPressureStrength, 
		float staticPressureRelaxation, int staticPressureIterations, float colorMixingStrength, boolean destroyByAge,
		float lifetimeGranularity); /*
		b2ParticleSystemDef def;
		def.radius = radius;
		def.pressureStrength = pressureStrength;
		def.dampingStrength = dampingStrength;
		def.elasticStrength = elasticStrength;
		def.springStrength = springStrength;
		def.viscousStrength = viscousStrength;
		def.surfaceTensionPressureStrength = surfaceTensionPressureStrength;
		def.surfaceTensionNormalStrength = surfaceTensionNormalStrength;
		def.repulsiveStrength = repulsiveStrength;
		def.powderStrength = powderStrength;
		def.ejectionStrength = ejectionStrength;
		def.staticPressureStrength = staticPressureStrength;
		def.staticPressureRelaxation = staticPressureRelaxation;
		def.staticPressureIterations = staticPressureIterations;
		def.colorMixingStrength = colorMixingStrength;
		def.destroyByAge = destroyByAge;
		def.lifetimeGranularity = lifetimeGranularity;
		
		b2World* world = (b2World*)worldAddr;
		return (jlong)world->CreateParticleSystem(&def);
	*/
	
	public void destroyParticleSystem() {
		mWorld.particleSystems.remove(addr);
		jniDestroyParticleSystem(mWorld.getAddress(), addr);
	}
	
	private native void jniDestroyParticleSystem(long worldAddr, long systemAddr); /*
		b2World* world = (b2World*)worldAddr;
		b2ParticleSystem* system = (b2ParticleSystem*)systemAddr;
		
		world->DestroyParticleSystem(system);
	*/
	
	/** @return Returns the index of the particle. */
	public int createParticle(ParticleDef pDef) {
		int flags;
		if(pDef.flags.size() == 0) flags = 0;
		else {
			flags = pDef.flags.get(0).getValue();
			for(int i = 1; i < pDef.flags.size(); i++) {
				flags = ((int)(flags | pDef.flags.get(i).getValue()));
			}
		}
		
		boolean addToGroup = true;
		if(pDef.group == null) addToGroup = false;
		
		return jniCreateParticle(addr, flags, pDef.position.x, pDef.position.y, pDef.velocitiy.x, pDef.velocitiy.y,
			(int) (pDef.color.getRed() * 255f), (int) (pDef.color.getGreen() * 255f), (int) (pDef.color.getBlue() * 255f), (int) (pDef.color.getAlpha() * 255f),
			pDef.lifetime, addToGroup, addToGroup ? pDef.group.addr : -1);
	}
	
	private native int jniCreateParticle(long addr, int pFlags, float pPositionX, float pPositionY, float pVelocityX, float pVelocityY, 
			int pColorR, int pColorG, int pColorB, int pColorA, float lifetime, boolean addToGroup, long groupAddr); /*
		b2ParticleDef particleDef;
		particleDef.flags = pFlags;
		particleDef.position.Set( pPositionX, pPositionY );
		particleDef.velocity.Set( pVelocityX, pVelocityY );
		particleDef.color.Set(pColorR, pColorG, pColorB, pColorA);
		particleDef.lifetime = lifetime;
		if(addToGroup) particleDef.group = (b2ParticleGroup*)groupAddr;
		
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 index = system->CreateParticle( particleDef );
		return (jint)index;
	*/

	/** Removes a particle
	 * @param pIndex The index of the particle given by createParticle() */
	public void destroyParticle(int pIndex) {
		jniDestroyParticle(addr, pIndex);
	}
	
	private native void jniDestroyParticle(long addr, int pIndex); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->DestroyParticle( pIndex, false );
	*/
	
	/** Destroy the Nth oldest particle in the system.
	* The particle is removed after the next b2World::Step().
	* @param pIndex of the Nth oldest particle to destroy, 0 will destroy the
	* oldest particle in the system, 1 will destroy the next oldest
	* particle etc. */
	public void destroyOldestParticle(int pIndex) {
		jniDestroyOldestParticle(addr, pIndex);
	}
	
	private native void jniDestroyOldestParticle(long addr, int index); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->DestroyOldestParticle(index, false);
	*/
	
	/** Removes all particles in the bounds of the shape
	 * @param pShape
	 * @param pTransform transformation of the shape
	 * @return the number of particles destroyed*/
	public int destroyParticleInShape(Shape pShape, Transform pTransform) {
		return jniDestroyParticleInShape(addr, pShape.getAddress(), pTransform.getPosition().x, pTransform.getPosition().y, pTransform.getRotation());
	}
	
	private native int jniDestroyParticleInShape(long addr, long pShapeAddr, float pTransformPosX, float pTransformPosY, float pAngle); /*
		b2Shape* shape = (b2Shape*)pShapeAddr;
		b2Transform transform;
		transform.Set( b2Vec2( pTransformPosX, pTransformPosY ), pAngle );
		
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jint)system->DestroyParticlesInShape( *shape, transform, false );
	*/

	public ParticleGroup createParticleGroup(ParticleGroupDef pGroupDef) {
		int flags;
		if(pGroupDef.flags.size() == 0) flags = 0;
		else {
			flags = pGroupDef.flags.get(0).getValue();
			for(int i = 1; i < pGroupDef.flags.size(); i++) {
				flags = ((int)(flags | pGroupDef.flags.get(i).getValue()));
			}
		}
		
		int groupFlags;
		if(pGroupDef.groupFlags.size() == 0) groupFlags = 0;
		else {
			groupFlags = pGroupDef.groupFlags.get(0).getValue();
			for(int i = 1; i < pGroupDef.groupFlags.size(); i++) {
				flags = ((int)(flags | pGroupDef.groupFlags.get(i).getValue()));
			}
		}
		
		float positionDataX = 0;
		float positionDataY = 0;
		boolean positionDataSet = pGroupDef.positionData != null;
		if(positionDataSet) {
			positionDataX = pGroupDef.positionData.x;
			positionDataY = pGroupDef.positionData.y;
		}
		
		long addrParticleGroup = jniCreateParticleGroup(addr, flags, groupFlags, pGroupDef.position.x, pGroupDef.position.y, pGroupDef.angle,
				pGroupDef.linearVelocity.x, pGroupDef.linearVelocity.y, pGroupDef.angularVelocity, 
				(int) (pGroupDef.color.getRed() * 255f), (int) (pGroupDef.color.getGreen() * 255f), 
				(int) (pGroupDef.color.getBlue() * 255f), (int) (pGroupDef.color.getAlpha() * 255f),
				pGroupDef.strength, pGroupDef.shape.getAddress(), pGroupDef.stride, pGroupDef.particleCount, positionDataSet,
				positionDataX, positionDataY, pGroupDef.lifetime, pGroupDef.group == null ? -1 : pGroupDef.group.addr);
		
		ParticleGroup group = new ParticleGroup(0);
		group.addr = addrParticleGroup;
		this.particleGroups.put(addrParticleGroup, group);
		return group;
	}
	
	private native long jniCreateParticleGroup(long addr, int pFlags, int pGroupFlags, float pPositionX, 
		float pPositionY, float pAngle, float pLinVelocityX, float pLinVelocityY, 
		float pAngularVelocity, int pColorR, int pColorG, int pColorB, int pColorA, 
		float pStrength, long pShapeAddr, float stride, int particleCount, boolean positionDataSet, 
		float positionDataX, float positionDataY, float lifetime, long groupAddr); /*
		b2ParticleGroupDef groupDef;
		groupDef.flags = pFlags;
		groupDef.groupFlags = pGroupFlags;
		groupDef.position.Set( pPositionX, pPositionY );
		groupDef.angle = pAngle;
		groupDef.linearVelocity.Set( pLinVelocityX, pLinVelocityY );
		groupDef.angularVelocity = pAngularVelocity;
		groupDef.color.Set( pColorR, pColorG, pColorB, pColorA );
		groupDef.strength = pStrength;
		groupDef.shape = (b2Shape*)pShapeAddr;
		groupDef.stride = stride;
		groupDef.particleCount = particleCount;
		groupDef.lifetime = lifetime;
		
		if(positionDataSet) {
			const b2Vec2 positionData = b2Vec2(positionDataX, positionDataY);
			groupDef.positionData = &positionData;
		}
		
		if(groupAddr != -1) {
			groupDef.group = (b2ParticleGroup*)groupAddr;
		}
		
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jlong)system->CreateParticleGroup( groupDef );
	*/
	
	/** Join two particle groups. This function is locked during callbacks.
	 * @param pGroupA first group. Expands to encompass the second group.
	 * @param pGroupB second group. It is destroyed. */
	public void joinParticleGroups(ParticleGroup pGroupA, ParticleGroup pGroupB) {
		jniJoinParticleGroups(addr, pGroupA.addr, pGroupB.addr);
	}
	
	private native void jniJoinParticleGroups(long addr, long addrA, long addrB); /*
		b2ParticleGroup* groupA = (b2ParticleGroup*)addrA;
		b2ParticleGroup* groupB = (b2ParticleGroup*)addrB;
	
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->JoinParticleGroups( groupA, groupB );
	*/
	
	public float[] getParticlePositionBufferX() {
		return jniGetParticlePositionBufferX(addr);
	}
	
	public float[] getParticlePositionBufferY() {
		return jniGetParticlePositionBufferY(addr);
	}
	
	private native float[] jniGetParticlePositionBufferX(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jfloatArray array;
		array = env->NewFloatArray((jsize) count);
		
		jfloat fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetPositionBuffer()[i].x;
		}
		
		env->SetFloatArrayRegion(array, 0, (jsize) count, fill);
 		return array;
	*/
	private native float[] jniGetParticlePositionBufferY(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count =  system->GetParticleCount();
		
		jfloatArray array;
		array = env->NewFloatArray((jsize) count);
		
		jfloat fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetPositionBuffer()[i].y;
		}
		
		env->SetFloatArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	private float[] positionBufferArray = new float[0];
	
	public float[] getParticlePositionBufferArray(boolean update) {
		if(!update) return positionBufferArray;
		int particleCount = getParticleCount();
		
		if(positionBufferArray.length != particleCount * 2) {
			positionBufferArray = new float[particleCount * 2];
		}
		
		jniUpdateParticlePositionBuffer(addr, positionBufferArray);
		return positionBufferArray;
	}
	
	private native void jniUpdateParticlePositionBuffer(long addr, float[] buffer); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jfloatArray array;
		
		for(int i = 0; i < count * 2; i += 2) {
			buffer[i] = system->GetPositionBuffer()[i / 2].x;
			buffer[i + 1] = system->GetPositionBuffer()[i / 2].y;
		}
	*/
	
	private final static ArrayList<Vector2> mVelocities = new ArrayList<Vector2>();
	
	/** Reloads the velocitybuffer from native code and returns it */
	public ArrayList<Vector2> getParticleVelocityBuffer() {
		updateParticleVelocitiyBuffer();
		return mVelocities;
	}
	
	private native float[] jniGetParticleVelocityBufferX(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jfloatArray array;
		array = env->NewFloatArray((jsize) count);
		
		jfloat fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetVelocityBuffer()[i].x;
		}
		
		env->SetFloatArrayRegion(array, 0, (jsize) count, fill);
 		return array;
	*/
	private native float[] jniGetParticleVelocityBufferY(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jfloatArray array;
		array = env->NewFloatArray((jsize) count);
		
		jfloat fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetVelocityBuffer()[i].y;
		}
		
		env->SetFloatArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	private final static ArrayList<Color> mColors = new ArrayList<Color>();
	
	/** Reloads the colorbuffer from native code and returns it */
	public ArrayList<Color> getParticleColorBuffer() {
		updateParticleColorBuffer();
		return mColors;
	}
	
	private native int[] jniGetParticleColorBufferR(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jintArray array;
		array = env->NewIntArray((jsize) count);
		
		jint fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetColorBuffer()[i].r;
		}
		
		env->SetIntArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	private native int[] jniGetParticleColorBufferG(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jintArray array;
		array = env->NewIntArray((jsize) count);
		
		jint fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetColorBuffer()[i].g;
		}
		
		env->SetIntArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	private native int[] jniGetParticleColorBufferB(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jintArray array;
		array = env->NewIntArray((jsize) count);
		
		jint fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetColorBuffer()[i].b;
		}
		
		env->SetIntArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	private native int[] jniGetParticleColorBufferA(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		int32 count = system->GetParticleCount();
		
		jintArray array;
		array = env->NewIntArray((jsize) count);
		
		jint fill[count];
		for(int i = 0; i < count; i++) {
			fill[i] = system->GetColorBuffer()[i].a;
		}
		
		env->SetIntArrayRegion(array, 0, (jsize) count, fill);
		return array;
	*/
	
	/** Reloads the velocitybuffer from native code */
	public void updateParticleVelocitiyBuffer() {
		mVelocities.clear();
		
		float[] x = jniGetParticleVelocityBufferX(addr);
		float[] y = jniGetParticleVelocityBufferY(addr);
		
		for(int i = 0; i < getParticleCount(); i++) {
			mVelocities.add(new Vector2(x[i], y[i]));
		}
	}
	
	/** Reloads the colorbuffer from native code */
	public void updateParticleColorBuffer() {
		mColors.ensureCapacity(getParticleCount());
		mColors.clear();
		
		int[] r = jniGetParticleColorBufferR(addr);
		int[] g = jniGetParticleColorBufferG(addr);
		int[] b = jniGetParticleColorBufferB(addr);
		int[] a = jniGetParticleColorBufferA(addr);
		
		for(int i = 0; i < getParticleCount(); i++) {
			mColors.add(new Color(r[i] / 255f, g[i] / 255f, b[i] / 255f, a[i] / 255f));
		}
	}
	
	/** returns the velocitybuffer without reloading it */
	public ArrayList<Vector2> getParticleVelocityBufferWithoutUpdate() {
		return mVelocities;
	}
	
	/** returns the colorbuffer without reloading it */
	public ArrayList<Color> getParticleColorBufferWithoutUpdate() {
		return mColors;
	}
	
	public void setParticleRadius(float pRadius) {
		jniSetParticleRadius(addr, pRadius);
	}
	
	private native void jniSetParticleRadius(long addr, float pRadius); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetRadius( pRadius );
	*/
	
	public float getParticleRadius() {
		return jniGetParticleRadius(addr);
	}
	
	private native float jniGetParticleRadius(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jfloat)system->GetRadius();
	*/
	
	/** The total count of particles currently in the simulation */
	public int getParticleCount() {
		return jniGetParticleCount(addr);
	}
	
	private native int jniGetParticleCount(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jint)system->GetParticleCount();
	*/
	
	public int getParticleGroupCount() {
		return jniGetParticleGroupCount(addr);
	}
	
	private native int jniGetParticleGroupCount(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jint)system->GetParticleGroupCount();
	*/
	
	/** Pause or unpause the particle system. When paused, b2World::Step()
	* skips over this particle system. All b2ParticleSystem function calls
	* still work.
	* @param pPaused is true to pause, false to un-pause. */
	public void setPaused(boolean pPaused) {
		jniSetPaused(addr, pPaused);
	}
	
	private native void jniSetPaused(long addr, boolean paused); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetPaused(paused);
	*/
	
	public boolean getPaused() {
		return jniGetPaused(addr);
	}
	
	private native boolean jniGetPaused(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jboolean)system->GetPaused();
	*/
	
	public void setParticleDensity(float pDensity) {
		jniSetParticleDensity(addr, pDensity);
	}
	
	private native void jniSetParticleDensity(long addr, float pDensity); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetDensity(pDensity);
	*/
	
	public float getParticleDensity() {
		return jniGetParticleDensity(addr);
	}
	
	private native float jniGetParticleDensity(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jfloat)system->GetDensity();
	*/
	
	public void setParticleGravityScale(float pGravityScale) {
		jniSetParticleGravityScale(addr, pGravityScale);
	}
	
	private native void jniSetParticleGravityScale(long addr, float pGravityScale); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetGravityScale(pGravityScale);
	*/
	
	public float getParticleGravityScale() {
		return jniGetParticleGravityScale(addr);
	}
	
	private native float jniGetParticleGravityScale(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jfloat)system->GetGravityScale();
	*/
	
	public void setParticleMaxCount(int pCount) {
		jniSetParticleMaxCount(addr, pCount);
	}
	
	private native void jniSetParticleMaxCount(long addr, float pCount); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetMaxParticleCount(pCount);
	*/
	
	public float getMaxParticleCount() {
		return jniGetMaxParticleCount(addr);
	}
	
	private native float jniGetMaxParticleCount(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jint)system->GetMaxParticleCount();
	*/
	
	public void setParticleDamping(float pDamping) {
		jniSetParticleDamping(addr, pDamping);
	}
	
	private native void jniSetParticleDamping(long addr, float pDamping); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		system->SetDamping(pDamping);
	*/
	
	public float getParticleDamping() {
		return jniGetParticleDamping(addr);
	}
	
	private native float jniGetParticleDamping(long addr); /*
		b2ParticleSystem* system = (b2ParticleSystem*)addr;
		return (jfloat)system->GetDamping();
	*/
	
	public String getVersionString() {
		return jniGetVersionString(mWorld.getAddress());
	}

	private native String jniGetVersionString(long worldAddr); /*
		b2World* world = (b2World*)worldAddr;
		const char* version = world->GetVersionString();
		return env->NewStringUTF(version);
	*/
}
