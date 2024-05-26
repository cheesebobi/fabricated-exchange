package com.skirlez.fabricatedexchange.item.rings;

import com.skirlez.fabricatedexchange.emc.EmcData;
import com.skirlez.fabricatedexchange.item.rings.base.FEShooterRing;
import com.skirlez.fabricatedexchange.util.SuperNumber;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.projectile.ArrowEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

import java.util.List;
import java.util.Random;

public class ArchangelsSmite extends FEShooterRing {

	public ArchangelsSmite(Settings settings) {
		super(settings);
	}

	@Override
	protected SuperNumber getProjectileEMC() {
		return EmcData.getItemEmc(Items.ARROW);
	}

	@Override
	protected void fireSingleProjectile(World world, PlayerEntity user, float speed, float divergence) {
		ArrowEntity arrow = new ArrowEntity(world, user);
		arrow.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
		Vec3d vec3d = user.getRotationVec(1.0F);
		arrow.setVelocity(vec3d.x, vec3d.y, vec3d.z, speed, divergence);
		arrow.pickupType = ArrowEntity.PickupPermission.DISALLOWED;
		world.spawnEntity(arrow);
	}

	@Override
	protected boolean fireHomingProjectile(World world, PlayerEntity user, float speed, float divergence) {
		var triggered = false;
		if (!user.getItemCooldownManager().isCoolingDown(this)) {
			List<LivingEntity> entities = world.getEntitiesByClass(LivingEntity.class, user.getBoundingBox().expand(20), entity -> entity != user);
			LivingEntity closestEntity = null;
			double closestDistance = Double.MAX_VALUE;

			for (LivingEntity entity : entities) {
				double distance = user.squaredDistanceTo(entity);
				if (distance < closestDistance && isVisible(user, entity, world)) {
					closestEntity = entity;
					closestDistance = distance;
				}
			}

			if (closestEntity != null) {
				Vec3d direction = closestEntity.getPos().subtract(user.getPos().offset(Direction.UP, 0.5)).normalize();
				ArrowEntity arrow = new ArrowEntity(world, user);
				arrow.updatePosition(user.getX(), user.getEyeY() - 0.1, user.getZ());
				arrow.setVelocity(direction.x, direction.y, direction.z, speed, divergence);
				arrow.pickupType = ArrowEntity.PickupPermission.DISALLOWED;
				world.spawnEntity(arrow);
				user.getItemCooldownManager().set(this, 5);
				triggered = true;
			}
		}
		return triggered;
	}

	@Override
	protected void fireChaosProjectile(World world, PlayerEntity user, float speed, float divergence) {
		Random random = new Random();

		Vec3d direction = new Vec3d(
				random.nextDouble() * 2 - 1,
				random.nextDouble() * 2 - 1,
				random.nextDouble() * 2 - 1
		).normalize();
		ArrowEntity arrow = new ArrowEntity(world, user);
		arrow.setPosition(user.getX() + direction.x * 2, user.getEyeY() - 1, user.getZ() + direction.z * 2);
		arrow.setVelocity(direction.x, direction.y, direction.z, speed, divergence);
		arrow.pickupType = ArrowEntity.PickupPermission.DISALLOWED;
		world.spawnEntity(arrow);
	}

	public static boolean isAngry(ItemStack stack) {
		if (stack.getItem() instanceof ArchangelsSmite item)
			return item.autoshoot;
		return false;
	}

}

