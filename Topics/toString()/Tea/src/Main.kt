open class Tea(val cost: Int, val volume: Int) {
    override fun toString(): String {
        return "Tea{cost=$cost, volume=$volume}"
    }
}

class BlackTea(val_cost: Int, val_volume: Int) : Tea(val_cost, val_volume) {
    override fun toString(): String {
        return "BlackTea{cost=${super.cost}, volume=${super.volume}}"
    }
}
