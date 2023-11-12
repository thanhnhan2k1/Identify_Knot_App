package com.example.identifyknotapp.ui.result

import com.example.identifyknotapp.data.model.WoodDescription
import com.example.identifyknotapp.data.model.WoodResponse

fun WoodResponse.toListWoodDescriptions() : List<WoodDescription> {
    val listResult = mutableListOf<WoodDescription>()
    listResult.add(WoodDescription(title = "Viet Nam Name", content = this.vietnamName))
    listResult.add(WoodDescription(title = "Scientific Name", content = this.scientificName))
    listResult.add(WoodDescription(title = "Commercial Name", content = this.commercialName))
    listResult.add(WoodDescription(title = "CategoryWood", content = this.categoryWood))
    listResult.add(WoodDescription(title = "Preservation", content = this.preservation))
    listResult.add(WoodDescription(title = "Family", content = this.family))
    listResult.add(WoodDescription(title = "Specific Gravity", content = this.specificGravity))
    listResult.add(WoodDescription(title = "Appendix Cites", content = this.appendixCites))
    listResult.add(WoodDescription(title = "Characteristic", content = this.characteristic))
    listResult.add(WoodDescription(title = "Note", content = this.note))
    listResult.add(WoodDescription(title = "Color", content = this.color))
    listResult.add(WoodDescription(title = "Geography Area", content = this.area))
    return listResult
}