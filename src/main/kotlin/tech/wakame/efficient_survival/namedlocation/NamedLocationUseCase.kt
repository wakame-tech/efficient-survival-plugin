package tech.wakame.efficient_survival.namedlocation

import org.bukkit.Location

interface INamedLocationUseCase {
    fun saveAll()

    fun loadAll()

    fun registerLocation(label: String, location: Location)

    fun getLocations(): List<NamedLocation>

    fun getLocation(label: String): NamedLocation?

    fun deleteLocation(label: String)
}

class NamedLocationUseCase(private val repo: INamedLocationRepository): INamedLocationUseCase {
    private val locations: MutableList<NamedLocation> = mutableListOf()

    override fun loadAll() {
        locations.clear()
        locations.addAll(repo.load())
    }

    override fun saveAll() {
        repo.save(locations)
    }

    override fun registerLocation(label: String, location: Location) {
        locations.add(NamedLocation(label, location))
    }

    override fun getLocations(): List<NamedLocation> {
        return locations
    }

    override fun getLocation(label: String): NamedLocation? {
        return locations.firstOrNull { it.label == label }
    }

    override fun deleteLocation(label: String) {
        locations.removeIf { it.label == label }
    }
}