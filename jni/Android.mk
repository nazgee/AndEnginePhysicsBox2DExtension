LOCAL_PATH := $(call my-dir)
include $(CLEAR_VARS)

LOCAL_MODULE    := gdx
LOCAL_C_INCLUDES := 

LOCAL_MODULE    := andenginephysicsbox2dextension
LOCAL_ARM_MODE := arm
LOCAL_SRC_FILES := Box2D/Collision/b2BroadPhase.cpp\
Box2D/Collision/b2CollideCircle.cpp\
Box2D/Collision/b2CollideEdge.cpp\
Box2D/Collision/b2CollidePolygon.cpp\
Box2D/Collision/b2Collision.cpp\
Box2D/Collision/b2Distance.cpp\
Box2D/Collision/b2DynamicTree.cpp\
Box2D/Collision/b2TimeOfImpact.cpp\
Box2D/Collision/Shapes/b2ChainShape.cpp\
Box2D/Collision/Shapes/b2CircleShape.cpp\
Box2D/Collision/Shapes/b2EdgeShape.cpp\
Box2D/Collision/Shapes/b2PolygonShape.cpp\
Box2D/Common/b2BlockAllocator.cpp\
Box2D/Common/b2Draw.cpp\
Box2D/Common/b2FreeList.cpp\
Box2D/Common/b2Math.cpp\
Box2D/Common/b2Settings.cpp\
Box2D/Common/b2StackAllocator.cpp\
Box2D/Common/b2Stat.cpp\
Box2D/Common/b2Timer.cpp\
Box2D/Common/b2TrackedBlock.cpp\
Box2D/Dynamics/b2Body.cpp\
Box2D/Dynamics/b2ContactManager.cpp\
Box2D/Dynamics/b2Fixture.cpp\
Box2D/Dynamics/b2Island.cpp\
Box2D/Dynamics/b2World.cpp\
Box2D/Dynamics/b2WorldCallbacks.cpp\
Box2D/Dynamics/Contacts/b2ChainAndCircleContact.cpp\
Box2D/Dynamics/Contacts/b2ChainAndPolygonContact.cpp\
Box2D/Dynamics/Contacts/b2CircleContact.cpp\
Box2D/Dynamics/Contacts/b2Contact.cpp\
Box2D/Dynamics/Contacts/b2ContactSolver.cpp\
Box2D/Dynamics/Contacts/b2EdgeAndCircleContact.cpp\
Box2D/Dynamics/Contacts/b2EdgeAndPolygonContact.cpp\
Box2D/Dynamics/Contacts/b2PolygonAndCircleContact.cpp\
Box2D/Dynamics/Contacts/b2PolygonContact.cpp\
Box2D/Dynamics/Joints/b2DistanceJoint.cpp\
Box2D/Dynamics/Joints/b2FrictionJoint.cpp\
Box2D/Dynamics/Joints/b2GearJoint.cpp\
Box2D/Dynamics/Joints/b2Joint.cpp\
Box2D/Dynamics/Joints/b2MotorJoint.cpp\
Box2D/Dynamics/Joints/b2MouseJoint.cpp\
Box2D/Dynamics/Joints/b2PrismaticJoint.cpp\
Box2D/Dynamics/Joints/b2PulleyJoint.cpp\
Box2D/Dynamics/Joints/b2RevoluteJoint.cpp\
Box2D/Dynamics/Joints/b2RopeJoint.cpp\
Box2D/Dynamics/Joints/b2WeldJoint.cpp\
Box2D/Dynamics/Joints/b2WheelJoint.cpp\
Box2D/Particle/b2Particle.cpp\
Box2D/Particle/b2ParticleGroup.cpp\
Box2D/Particle/b2ParticleSystem.cpp\
Box2D/Particle/b2VoronoiDiagram.cpp\
Box2D/Rope/b2Rope.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Body.cpp\
Box2D/com.badlogic.gdx.physics.box2d.ChainShape.cpp\
Box2D/com.badlogic.gdx.physics.box2d.CircleShape.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Contact.cpp\
Box2D/com.badlogic.gdx.physics.box2d.ContactImpulse.cpp\
Box2D/com.badlogic.gdx.physics.box2d.EdgeShape.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Fixture.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Joint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.DistanceJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.FrictionJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.GearJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.MouseJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.PrismaticJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.PulleyJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.RevoluteJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.RopeJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.WeldJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.joints.WheelJoint.cpp\
Box2D/com.badlogic.gdx.physics.box2d.liquidfun.ParticleGroup.cpp\
Box2D/com.badlogic.gdx.physics.box2d.liquidfun.ParticleSystem.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Manifold.cpp\
Box2D/com.badlogic.gdx.physics.box2d.PolygonShape.cpp\
Box2D/com.badlogic.gdx.physics.box2d.Shape.cpp\
Box2D/com.badlogic.gdx.physics.box2d.World.cpp
				   
LOCAL_CFLAGS := -DFIXED_POINT -ffast-math -O3 -Wall -I$(LOCAL_PATH) -D_ARM_ASSEM_ -DANDROID
LOCAL_CPPFLAGS := -DFIXED_POINT -I$LOCAL_PATH/libvorbis/ -D_ARM_ASSEM_
LOCAL_LDLIBS := -llog
LOCAL_ARM_MODE  := arm

include $(BUILD_SHARED_LIBRARY)
