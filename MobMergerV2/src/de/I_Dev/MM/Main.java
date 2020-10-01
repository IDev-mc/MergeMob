package de.I_Dev.MM;

import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	@Override
	public void onEnable() {
		super.onEnable();
		Bukkit.getPluginManager().registerEvents(this, this);
		worldScheduler();
	}

	/*
	 * Schedules trough every world and checks for merging entity's
	 */
	private void worldScheduler() {
		Bukkit.getScheduler().scheduleSyncRepeatingTask(this, new Runnable() {

			@Override
			public void run() {
				for (World w : Bukkit.getWorlds()) {
					for (Entity entity : w.getEntities()) {
						for (Entity secondentity : entity.getNearbyEntities(10, 10, 10)) {
							mergeEntitys(entity, secondentity);
						}
					}
				}
			}
		}, 20L, 20L);
	}

	/*
	 * Removes secondEntity and adds one "size" to the main Entity
	 */
	private void mergeEntitys(Entity mainEntity, Entity secondEntity) {
		if (!mainEntity.isValid()) return;
		if (mainEntity.getType() != secondEntity.getType()) return;
		if (mainEntity.getTicksLived() < 20) return;

		int size = getEntitySize(mainEntity) + getEntitySize(secondEntity);
		mainEntity.setCustomName(size + "x" + convertTypeToName(mainEntity.getType()));
		secondEntity.remove();
	}

	/*
	 * If size is "size < 2" moves on to respawnEntity()
	 */
	@EventHandler
	public void entityDeathEvent(EntityDeathEvent e) {
		LivingEntity entity = e.getEntity();
		int size = getEntitySize(entity);
		if (size < 2)
			return;
		respawnEntity(entity, size);
	}

	/*
	 * Respawns an entity with size-1 of the entity before
	 */
	private void respawnEntity(Entity entity, int size) {
		Entity newEntity = entity.getWorld().spawnEntity(entity.getLocation(), entity.getType());
		String name = convertTypeToName(newEntity.getType());
		newEntity.setCustomName((size - 1) + "x" + name);
	}

	/*
	 * Replaces every _ to space and upper cases every split beginning
	 */
	private String convertTypeToName(EntityType entitytype) {
		if (entitytype == null) return "";
		
		String[] entitytypesplit = entitytype.toString().replaceAll("_", " ").toLowerCase().split(" ");
		String craftname = "";
		for (String s : entitytypesplit) {
			craftname = craftname + " " + s;
		}

		return craftname;
	}

	/*
	 * Reads the entity "size" out of the custom name from the entity
	 */
	private int getEntitySize(Entity entity) {
		if (entity.getCustomName() == null) return 1;
		
		return Integer.parseInt(entity.getCustomName().split("x")[0]);
	}
}
