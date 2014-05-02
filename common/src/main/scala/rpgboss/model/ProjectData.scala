package rpgboss.model
import rpgboss.model.resource.RpgMap
import java.io.File
import rpgboss.lib.JsonUtils

case class ProjectDataStartup(
  var startingLoc: MapLoc = MapLoc(RpgMap.generateName(1), 5.5f, 5.5f),
  var startingParty: Array[Int] = Array(0),

  var titlePic: String = "LordSpirit.jpg",
  var titleMusic: Option[SoundSpec] = None,

  var windowskin: String = "LastPhantasmScanlines.png",
  var msgfont: String = "VeraMono.ttf",
  var fontsize: Int = 24,

  var soundCursor: Option[SoundSpec] =
    Some(SoundSpec("rpgboss-menu/MenuCursor.mp3")),
  var soundSelect: Option[SoundSpec] =
    Some(SoundSpec("rpgboss-menu/MenuSelect.mp3")),
  var soundCancel: Option[SoundSpec] =
    Some(SoundSpec("rpgboss-menu/MenuCancel.mp3")),
  var soundCannot: Option[SoundSpec] =
    Some(SoundSpec("rpgboss-menu/MenuCannot.mp3")))

case class ProjectDataEnums(
  var animations: Array[Animation] = Array(Animation()),
  var characters: Array[Character] = ProjectData.defaultCharacters,
  var classes: Array[CharClass] = Array(CharClass()),
  var elements: Array[String] = ProjectData.defaultElements,
  var enemies: Array[Enemy] = Array(Enemy()),
  var encounters: Array[Encounter] = Array(Encounter()),
  var equipTypes: Array[String] = ProjectData.defaultEquipTypes,
  var items: Array[Item] = Array(Item()),
  var skills: Array[Skill] = Array(Skill()),
  var statusEffects: Array[StatusEffect] = Array(StatusEffect()))

case class ProjectData(
  var uuid: String = java.util.UUID.randomUUID().toString(),
  var title: String = "Untitled Project",
  var recentMapName: String = "",
  var lastCreatedMapId: Int = 1, // Start at 1)
  var startup: ProjectDataStartup = ProjectDataStartup(),
  var enums: ProjectDataEnums = ProjectDataEnums()) {
  
  def writeEnums(dir: File) = {
    def writeModel[T <: AnyRef](name: String, model: T) =
      JsonUtils.writeModelToJson(new File(dir, "%s.json".format(name)), model)
    
    writeModel("animations", enums.animations)
    writeModel("characters", enums.characters)
    writeModel("classes", enums.classes)
    writeModel("elements", enums.elements)
    writeModel("enemies", enums.enemies)
    writeModel("encounters", enums.encounters)
    writeModel("equipTypes", enums.equipTypes)
    writeModel("items", enums.items)
    writeModel("skills", enums.skills)
    writeModel("statusEffects", enums.statusEffects)
  }
  
  def writeRootWithoutEnums(dir: File) = {
    val enumsStripped = copy(enums = null)
    def writeModel[T <: AnyRef](name: String, model: T) =
      JsonUtils.writeModelToJson(new File(dir, "%s.json".format(name)), model)
    
    writeModel("rpgproject", enumsStripped)
  }
  
  def write(dir: File) = {
    writeRootWithoutEnums(dir)
    writeEnums(dir)
  }
}

object ProjectData {
  def read(dir: File): Option[ProjectData] = {
    val modelOpt = JsonUtils.readModelFromJson[ProjectData](
      new File(dir, "rpgproject.json"))

    // Executes the setter only if it successfully reads it from file.
    def readModel[T <: AnyRef](
      name: String, setter: T => Unit)(implicit m: Manifest[T]) = {
      val opt =
        JsonUtils.readModelFromJson[T](new File(dir, "%s.json".format(name)))

      if (opt.isDefined)
        setter(opt.get)
    }

    modelOpt.foreach { model =>
      val enums = model.enums
      readModel[Array[Animation]]("animations", enums.animations = _)
      readModel[Array[Character]]("characters", enums.characters = _)
      readModel[Array[CharClass]]("classes", enums.classes = _)
      readModel[Array[String]]("elements", enums.elements = _)
      readModel[Array[Enemy]]("enemies", enums.enemies = _)
      readModel[Array[Encounter]]("encounters", enums.encounters = _)
      readModel[Array[String]]("equipTypes", enums.equipTypes = _)
      readModel[Array[Item]]("items", enums.items = _)
      readModel[Array[Skill]]("skills", enums.skills = _)
      readModel[Array[StatusEffect]]("statusEffects", enums.statusEffects = _)
    }

    modelOpt
  }

  def defaultCharacters = Array(
    Character("Pando", sprite = Some(SpriteSpec("vx_chara01_a.png", 4))),
    Character("Estine", sprite = Some(SpriteSpec("vx_chara01_a.png", 1))),
    Character("Leoge", sprite = Some(SpriteSpec("vx_chara01_a.png", 3))),
    Character("Graven", sprite = Some(SpriteSpec("vx_chara01_a.png", 2))),
    Character("Carona", sprite = Some(SpriteSpec("vx_chara01_a.png", 6))))

  def defaultElements = Array(
    "Untyped",

    "Blunt",
    "Piercing",
    "Slashing",

    "Fire",
    "Cold",
    "Electric",
    "Earth",

    "Life",
    "Death",
    "Order",
    "Chaos"
  )

  def defaultEquipTypes = Array(
    "Weapon",
    "Offhand",
    "Armor",
    "Head",
    "Accessory")
}
