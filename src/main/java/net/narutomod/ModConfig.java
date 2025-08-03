package net.narutomod;

import net.minecraftforge.common.config.Config;

@Config(modid = NarutomodMod.MODID)
@ElementsNarutomodMod.ModElement.Tag
public class ModConfig extends ElementsNarutomodMod.ModElement {

	public static Techniques TECHNIQUES = new Techniques();
	public static Items ITEMS = new Items();
	public static WingedSusanoo WINGED_SUSANOO = new WingedSusanoo();
	public static Dojutsu DOJUTSU = new Dojutsu();

	public ModConfig(ElementsNarutomodMod instance) {
		super(instance, 837);
	}

	@Config.RangeDouble(min = 1, max = 10000000)
	@Config.Comment({"Maximum NINJA XP gainable.(Default: 100,000)"})
	public static double MAX_NINJA_XP = 100000;

	@Config.RangeInt(min = 1, max = 1000)
	@Config.Comment({"Jutsu XP gained on damage (Default: 1)" })
	public static int JUTSU_XP_GAIN = 1;
	
	@Config.Comment("If enabled tailed beasts spawn naturally around the world.")
	public static boolean SPAWN_TAILED_BEASTS = true;

    @Config.Comment("If true KG will be auto assigned to players in due time, after 300 ninjaXp reachched.")
	public static boolean AUTO_KEKKEIGENKAI_ASSIGNMENT = true;

	@Config.Comment("If enabled players has a chance of spawning as jinchuriki.")
	public static boolean SPAWN_AS_JINCHURIKI = true;

	@Config.Comment("If enabled, rinnegan/tenseigan/ems gained without the prerequisite achievements will be removed.")
	public static boolean REMOVE_CHEAT_DOJUTSUS = false;

	@Config.Comment("Itachi's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_ITACHI = 1;

	@Config.Comment("Kisame's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_KISAME = 1;

	@Config.Comment("sasori's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_SASORI = 1;

	@Config.Comment("sasori's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_DEIDARA = 1;

	@Config.Comment("Hidan's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_HIDAN = 1;

	@Config.Comment("Konan's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_KONAN = 1;

	@Config.Comment("Zabuza's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_ZABUZA = 1;

	@Config.Comment("White zetsu's spawn weight (0~20). 0 to stop spawning.")
	public static int SPAWN_WEIGHT_WHITEZETSU = 10;

	@Config.Comment("Whether or not bosses are aggressive on sight")
	public static boolean AGGRESSIVE_BOSSES = false;

	@Config.Comment("Stupid arms in the back Naruto run animation")
	public static boolean NARUTO_RUN = true;

	@Config.Comment("Itachi's chance to be real (1~100). Lower value means higher chance. 1 means it will be real everytime.")
	public static int ITACHI_REAL_CHANCE = 10;

	@Config.Comment("Chakra regeneration rate. 0.006 means 0.6% of your max chakra every 4 seconds")
	public static float CHAKRA_REGEN_RATE = 0.006F;

	@Config.Comment("Disable this to not allow any jutsu scrolls in loot chests")
	public static boolean ENABLE_JUTSU_SCROLLS_IN_LOOTCHESTS = true;

	@Config.Comment("Amaterasu flame on block duration (reference: vanilla fire is 3)")
	public static int AMATERASU_BLOCK_DURATION = 100;

	@Config.Comment("Ninja XP gain multiplier (higher value gains NinjaXp faster. default=0.5)")
	public static double NINJAXP_MULTIPLIER = 0.5D;

	@Config.Comment("Max Chakra Multiplier an entity could have before being squished to death, (Default: 4)")
	public static double MAX_CHAKRA_LIMIT_BEFORE_DEATH = 4;

	public static class Techniques {
		@Config.RangeDouble(min = 1, max = 256)
		@Config.Comment({"Maximum Amenotejikara teleportation range. (Default: 40)"})
		public double AMENOTEJIKARA_RANGE = 40;
		@Config.RangeDouble(min = 1, max = 256)
		@Config.Comment({"Maximum Universal Pull range. (Default: 50)"})
		public double UNIVERSAL_PULL_RANGE = 50;
		
		@Config.RangeDouble(min = 1, max = 5)
		@Config.Comment({"Player chakra multiplied by this number on Sage Mode entry. (Default: 1.6)"})
		public double SAGE_MODE_CHAKRA_MULTIPLIER = 1.6;

		@Config.RangeDouble(min = 0, max = 10000)
		@Config.Comment({"Chakra consumed when using the Hiraishin Teleport Behind. (Default: 500)"})
		public double HIRAISHIN_TELEPORT_BEHIND_CHAKRA_USAGE = 500;

		@Config.RangeDouble(min = 0, max = 500)
		@Config.Comment({"Maximum charge allowed for Shinra Tensei. (Default: 100)"})
		public double MAX_SHINRA_TENSEI_POWER = 100;
	}

	public static class Items {
		@Config.Comment({"Black Receivers cause slowness. (Default: true)"})
		public boolean BLACK_RECEIVER_SLOWNESS = true;

		@Config.RangeDouble(min = 0, max = 256)
		@Config.Comment({"Black Receivers Attack Damage. (Default: 10)"})
		public double BLACK_RECEIVER_ATTACK_DAMAGE= 10;

		@Config.RangeDouble(min = -256, max = 256)
		@Config.Comment({"Black Receivers Attack Speed. (Default: -2.4)"})
		public double BLACK_RECEIVER_ATTACK_SPEED= -2.4;
	}

	public static class WingedSusanoo {
		@Config.RangeDouble(min = 1, max = 50)
		@Config.Comment({"(Default: 8)"})
		public float MODEL_SCALE = 8;

		@Config.RangeDouble(min = 1, max = 200)
		@Config.Comment({" Y Offset where player lies within Susanoo. (Adjust with MODEL_SCALE) (Default: 14)"})
		public double PLAYER_Y_OFFSET = 14;
		
		@Config.RangeDouble(min = 1, max = 512)
		@Config.Comment({"(Default: 43)"})
		public float MAX_HEALTH = 43;

		@Config.RangeDouble(min = 1, max = 100)
		@Config.Comment({"Maximum Amenotejikara teleportation range. (Default: 12)"})
		public double SWORD_REACH = 12;
	}
	public static class Dojutsu {
		@Config.Comment("Chance of awakening a Rinnegan after eating Zetsu flesh with EMS on. After succeeding the chance, it's then a 0.01% chance per second of awakening with EMS in inventory. (Exactly like KGs) (Default: 5)")
		@Config.RangeDouble(min = 0, max = 100)
		public double RINNEGAN_AWAKEN_CHANCE = 5;
		
		@Config.Comment("Chance of awakening a Rinnegan Tomoe instead on Rinnegan Awakening. (Default: 50)")
		@Config.RangeDouble(min = 0, max = 100)
		public double RINNEGAN_TOMOE_AWAKEN_CHANCE = 50;

		@Config.Comment("Chance of unlocking Kekkei Mora upon becoming the Ten Tail's Jinchuuriki.  (Default: 50)")
		@Config.RangeDouble(min = 0, max = 100)
		public double KEKKEI_MORA_UNLOCK_CHANCE = 50;
		
		@Config.RangeInt(min = 0, max = 3600)
		@Config.Comment("Sharingan's Lock On cooldown in seconds. (applied once Lock On effect runs out) (Default: 60)")
		public int SHARINGAN_LOCK_ON_COOLDOWN = 60;

		@Config.RangeDouble(min = 1, max = 256)
		@Config.Comment({"Sharingan's Lock On default range.", "Value is multiplied by 1.25x if Mangekyo, 1.5x if Eternal, and 2x if Rinnegan Tomoe. (Default: 50)"})
		public double SHARINGAN_LOCK_ON_RANGE = 50;
	}
}
