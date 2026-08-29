package com.example.data.util

import com.example.data.model.BreakdownItem
import com.example.data.model.OrderProcessStep
import com.example.data.model.QuoteOption
import com.example.data.model.QuoteState
import org.json.JSONArray
import org.json.JSONObject

object JsvJsonHelper {

    fun quoteStateToJson(state: QuoteState): String {
        val json = JSONObject()
        json.put("quote_type", state.quoteType)
        json.put("brand_title", state.brandTitle)
        json.put("brand_sub", state.brandSub)
        json.put("sec_title_1", state.secTitle1)
        json.put("sec_title_2", state.secTitle2)
        json.put("sec_title_3", state.secTitle3)
        json.put("sec_title_4", state.secTitle4)
        json.put("doc_title", state.docTitle)
        json.put("doc_subtitle", state.docSubtitle)
        json.put("folio_ref", state.folioRef)
        json.put("date_str", state.dateStr)
        json.put("primary_color", state.primaryColor)
        json.put("dark_color", state.darkColor)
        json.put("page_mode", state.pageMode)
        json.put("porcentaje_comision", state.porcentajeComision)

        // Visibility flags
        json.put("show_sec_title_1", state.showSecTitle1)
        json.put("show_sec_title_2", state.showSecTitle2)
        json.put("show_sec_title_3", state.showSecTitle3)
        json.put("show_sec_title_4", state.showSecTitle4)
        json.put("show_donor_vehicle", state.showDonorVehicle)
        json.put("show_year_range", state.showYearRange)
        json.put("show_engine_range", state.showEngineRange)
        json.put("show_item_location", state.showItemLocation)
        json.put("show_part_name", state.showPartName)
        json.put("show_part_oem", state.showPartOem)
        json.put("show_part_eng", state.showPartEng)
        json.put("show_part_trans", state.showPartTrans)
        json.put("show_part_condition", state.showPartCondition)
        json.put("show_part_origin", state.showPartOrigin)
        json.put("show_tech_verify", state.showTechVerify)
        json.put("show_image_section", state.showImageSection)
        json.put("show_breakdown_table", state.showBreakdownTable)
        json.put("show_logistics", state.showLogistics)
        json.put("show_guarantee", state.showGuarantee)
        json.put("show_item_photos", state.showItemPhotos)
        json.put("show_placeholder_text", state.showPlaceholderText)
        json.put("show_img_caption", state.showImgCaption)

        // Specs
        json.put("donor_vehicle", state.donorVehicle)
        json.put("year_range", state.yearRange)
        json.put("engine_range", state.engineRange)
        json.put("item_location", state.itemLocation)
        json.put("part_name", state.partName)
        json.put("part_oem", state.partOem)
        json.put("part_eng", state.partEng)
        json.put("part_system", state.partSystem)
        json.put("part_condition", state.partCondition)
        json.put("part_origin", state.partOrigin)
        json.put("tech_verify_note", state.techVerifyNote)
        json.put("img_stock_local", state.imgStockLocal)
        json.put("img_caption_text", state.imgCaptionText)

        // Breakdown items
        val itemsArray = JSONArray()
        for (item in state.breakdownItems) {
            val itemObj = JSONObject()
            itemObj.put("concept", item.concept)
            itemObj.put("qty", item.qty)
            itemObj.put("condition", item.condition)
            itemObj.put("price_venta", item.priceVenta.toString())
            itemObj.put("price_cambio", item.priceCambio.toString())
            itemObj.put("img_filename", item.imgFilename)
            itemsArray.put(itemObj)
        }
        json.put("breakdown_items", itemsArray)

        // Logistics & Guarantee
        json.put("logistics_title", state.logisticsTitle)
        json.put("logistics_text", state.logisticsText)
        json.put("guarantee_title", state.guaranteeTitle)
        json.put("guarantee_text", state.guaranteeText)

        // Options
        val optionsArray = JSONArray()
        for (opt in state.options) {
            val optObj = JSONObject()
            optObj.put("title", opt.title)
            optObj.put("price", opt.price)
            optObj.put("description", opt.description)
            optObj.put("condition", opt.condition)
            optObj.put("delivery", opt.delivery)
            optObj.put("warranty", opt.warranty)
            optObj.put("img_filename", opt.imgFilename)
            optionsArray.put(optObj)
        }
        json.put("options", optionsArray)

        // Proceso Pedido
        val procesoObj = JSONObject()
        procesoObj.put("titulo_seccion", state.procesoTituloSeccion)
        procesoObj.put("mostrar", state.procesoMostrar)
        procesoObj.put("mostrar_titulo", state.procesoMostrarTitulo)
        procesoObj.put("mostrar_numeros", state.procesoMostrarNumeros)
        val pasosArray = JSONArray()
        for (paso in state.procesoPasos) {
            val pasoObj = JSONObject()
            pasoObj.put("titulo", paso.title)
            pasoObj.put("descripcion", paso.description)
            pasoObj.put("marcado", paso.marked)
            pasosArray.put(pasoObj)
        }
        procesoObj.put("pasos", pasosArray)
        json.put("proceso_pedido", procesoObj)

        return json.toString(2)
    }

    fun jsonToQuoteState(jsonStr: String): QuoteState {
        return try {
            val json = JSONObject(jsonStr)
            val default = QuoteState()

            val items = mutableListOf<BreakdownItem>()
            val itemsArr = json.optJSONArray("breakdown_items")
            if (itemsArr != null) {
                for (i in 0 until itemsArr.length()) {
                    val obj = itemsArr.optJSONObject(i) ?: continue
                    items.add(
                        BreakdownItem(
                            concept = obj.optString("concept", "Pieza OEM"),
                            qty = obj.optString("qty", "1"),
                            condition = obj.optString("condition", "OEM"),
                            priceVenta = obj.optString("price_venta", "0").toDoubleOrNull() ?: 0.0,
                            priceCambio = obj.optString("price_cambio", "0").toDoubleOrNull() ?: 0.0,
                            imgFilename = obj.optString("img_filename", "")
                        )
                    )
                }
            }

            val opts = mutableListOf<QuoteOption>()
            val optsArr = json.optJSONArray("options")
            if (optsArr != null) {
                for (i in 0 until optsArr.length()) {
                    val obj = optsArr.optJSONObject(i) ?: continue
                    opts.add(
                        QuoteOption(
                            title = obj.optString("title", "Opción"),
                            price = obj.optDouble("price", 0.0),
                            description = obj.optString("description", ""),
                            condition = obj.optString("condition", "OEM"),
                            delivery = obj.optString("delivery", "3-5 días"),
                            warranty = obj.optString("warranty", "30 días"),
                            imgFilename = obj.optString("img_filename", "")
                        )
                    )
                }
            }

            val pasos = mutableListOf<OrderProcessStep>()
            val procesoObj = json.optJSONObject("proceso_pedido")
            val pasosArr = procesoObj?.optJSONArray("pasos")
            if (pasosArr != null) {
                for (i in 0 until pasosArr.length()) {
                    val obj = pasosArr.optJSONObject(i) ?: continue
                    pasos.add(
                        OrderProcessStep(
                            title = obj.optString("titulo", ""),
                            description = obj.optString("descripcion", ""),
                            marked = obj.optBoolean("marcado", false)
                        )
                    )
                }
            }

            default.copy(
                quoteType = json.optString("quote_type", default.quoteType),
                brandTitle = json.optString("brand_title", default.brandTitle),
                brandSub = json.optString("brand_sub", default.brandSub),
                secTitle1 = json.optString("sec_title_1", default.secTitle1),
                secTitle2 = json.optString("sec_title_2", default.secTitle2),
                secTitle3 = json.optString("sec_title_3", default.secTitle3),
                secTitle4 = json.optString("sec_title_4", default.secTitle4),
                docTitle = json.optString("doc_title", default.docTitle),
                docSubtitle = json.optString("doc_subtitle", default.docSubtitle),
                folioRef = json.optString("folio_ref", default.folioRef),
                dateStr = json.optString("date_str", default.dateStr),
                primaryColor = json.optString("primary_color", default.primaryColor),
                darkColor = json.optString("dark_color", default.darkColor),
                pageMode = json.optString("page_mode", default.pageMode),
                porcentajeComision = json.optDouble("porcentaje_comision", default.porcentajeComision),

                showSecTitle1 = json.optBoolean("show_sec_title_1", default.showSecTitle1),
                showSecTitle2 = json.optBoolean("show_sec_title_2", default.showSecTitle2),
                showSecTitle3 = json.optBoolean("show_sec_title_3", default.showSecTitle3),
                showSecTitle4 = json.optBoolean("show_sec_title_4", default.showSecTitle4),
                showDonorVehicle = json.optBoolean("show_donor_vehicle", default.showDonorVehicle),
                showYearRange = json.optBoolean("show_year_range", default.showYearRange),
                showEngineRange = json.optBoolean("show_engine_range", default.showEngineRange),
                showItemLocation = json.optBoolean("show_item_location", default.showItemLocation),
                showPartName = json.optBoolean("show_part_name", default.showPartName),
                showPartOem = json.optBoolean("show_part_oem", default.showPartOem),
                showPartEng = json.optBoolean("show_part_eng", default.showPartEng),
                showPartTrans = json.optBoolean("show_part_trans", default.showPartTrans),
                showPartCondition = json.optBoolean("show_part_condition", default.showPartCondition),
                showPartOrigin = json.optBoolean("show_part_origin", default.showPartOrigin),
                showTechVerify = json.optBoolean("show_tech_verify", default.showTechVerify),
                showImageSection = json.optBoolean("show_image_section", default.showImageSection),
                showBreakdownTable = json.optBoolean("show_breakdown_table", default.showBreakdownTable),
                showLogistics = json.optBoolean("show_logistics", default.showLogistics),
                showGuarantee = json.optBoolean("show_guarantee", default.showGuarantee),
                showItemPhotos = json.optBoolean("show_item_photos", default.showItemPhotos),
                showPlaceholderText = json.optBoolean("show_placeholder_text", default.showPlaceholderText),
                showImgCaption = json.optBoolean("show_img_caption", default.showImgCaption),

                donorVehicle = json.optString("donor_vehicle", default.donorVehicle),
                yearRange = json.optString("year_range", default.yearRange),
                engineRange = json.optString("engine_range", default.engineRange),
                itemLocation = json.optString("item_location", default.itemLocation),
                partName = json.optString("part_name", default.partName),
                partOem = json.optString("part_oem", default.partOem),
                partEng = json.optString("part_eng", default.partEng),
                partSystem = json.optString("part_system", default.partSystem),
                partCondition = json.optString("part_condition", default.partCondition),
                partOrigin = json.optString("part_origin", default.partOrigin),
                techVerifyNote = json.optString("tech_verify_note", default.techVerifyNote),
                imgStockLocal = json.optString("img_stock_local", default.imgStockLocal),
                imgCaptionText = json.optString("img_caption_text", default.imgCaptionText),

                breakdownItems = if (items.isNotEmpty()) items else default.breakdownItems,
                logisticsTitle = json.optString("logistics_title", default.logisticsTitle),
                logisticsText = json.optString("logistics_text", default.logisticsText),
                guaranteeTitle = json.optString("guarantee_title", default.guaranteeTitle),
                guaranteeText = json.optString("guarantee_text", default.guaranteeText),

                options = if (opts.isNotEmpty()) opts else default.options,
                procesoTituloSeccion = procesoObj?.optString("titulo_seccion", default.procesoTituloSeccion) ?: default.procesoTituloSeccion,
                procesoMostrar = procesoObj?.optBoolean("mostrar", default.procesoMostrar) ?: default.procesoMostrar,
                procesoMostrarTitulo = procesoObj?.optBoolean("mostrar_titulo", default.procesoMostrarTitulo) ?: default.procesoMostrarTitulo,
                procesoMostrarNumeros = procesoObj?.optBoolean("mostrar_numeros", default.procesoMostrarNumeros) ?: default.procesoMostrarNumeros,
                procesoPasos = if (pasos.isNotEmpty()) pasos else default.procesoPasos
            )
        } catch (e: Exception) {
            QuoteState()
        }
    }

    fun exportToMarkdown(state: QuoteState): String {
        val jsonStr = quoteStateToJson(state)
        val sb = StringBuilder()
        sb.append("# ${state.brandTitle}\n")
        sb.append("**${state.brandSub}**\n")
        sb.append("**COTIZACIÓN:** ${state.partName} - ${state.donorVehicle}\n")
        sb.append("**FOLIO:** ${state.folioRef} | **FECHA:** ${state.dateStr}\n\n")
        sb.append("---\n\n")
        if (state.quoteType == "stock_local") {
            sb.append("### Ficha Técnica\n")
            sb.append("- **Vehículo:** ${state.donorVehicle} (${state.yearRange})\n")
            sb.append("- **Motor:** ${state.engineRange}\n")
            sb.append("- **Pieza:** ${state.partName}\n")
            sb.append("- **OEM:** ${state.partOem}\n\n")
            sb.append("### Opciones de Precio\n")
            for (item in state.breakdownItems) {
                sb.append("- **${item.concept}:** Al Cambio: $${String.format("%,.2f", item.priceCambio)} MXN | Venta Directa: $${String.format("%,.2f", item.priceVenta)} MXN\n")
            }
        } else {
            sb.append("### Sobre Pedido\n")
            for (opt in state.options) {
                sb.append("- **${opt.title}:** $${String.format("%,.2f", opt.price)} MXN (${opt.delivery})\n")
            }
        }
        sb.append("\n---\n\n")
        sb.append("<!-- JSV_STATE_JSON: $jsonStr -->\n")
        return sb.toString()
    }

    fun importFromMarkdown(mdContent: String): QuoteState? {
        val marker = "JSV_STATE_JSON:"
        val idx = mdContent.indexOf(marker)
        if (idx != -1) {
            var snippet = mdContent.substring(idx + marker.length)
            if (snippet.contains("-->")) {
                snippet = snippet.substringBefore("-->")
            }
            val start = snippet.indexOf("{")
            val end = snippet.lastIndexOf("}")
            if (start != -1 && end != -1 && end > start) {
                val jsonPart = snippet.substring(start, end + 1)
                return jsonToQuoteState(jsonPart)
            }
        }

        // Direct json fallback
        val s = mdContent.indexOf("{")
        val e = mdContent.lastIndexOf("}")
        if (s != -1 && e != -1 && e > s) {
            return try {
                jsonToQuoteState(mdContent.substring(s, e + 1))
            } catch (ex: Exception) {
                null
            }
        }
        return null
    }

    fun formatForWhatsApp(state: QuoteState): String {
        val sb = StringBuilder()
        sb.append("🛠️ *${state.brandTitle}*\n")
        sb.append("${state.brandSub}\n\n")
        sb.append("📄 *${state.docTitle}*\n")
        sb.append("🔖 *Folio:* ${state.folioRef} | 📅 ${state.dateStr}\n\n")
        sb.append("🚗 *Vehículo Donante:* ${state.donorVehicle}\n")
        sb.append("📅 *Años Compatibles:* ${state.yearRange}\n")
        sb.append("⚙️ *Motor:* ${state.engineRange}\n")
        sb.append("🔩 *Pieza:* ${state.partName}\n")
        if (state.partOem.isNotBlank()) sb.append("🏷️ *No. OEM:* ${state.partOem}\n")
        sb.append("✨ *Condición:* ${state.partCondition}\n\n")

        if (state.quoteType == "stock_local") {
            sb.append("💰 *OPCIONES Y PRECIOS DISPONIBLES:*\n")
            for (item in state.breakdownItems) {
                sb.append("• *${item.concept}* (${item.condition})\n")
                sb.append("   - Al Cambio: *$${String.format("%,.0f", item.priceCambio)} MXN*\n")
                sb.append("   - Venta Directa: *$${String.format("%,.0f", item.priceVenta)} MXN*\n")
            }
        } else {
            sb.append("📦 *OPCIONES SOBRE PEDIDO:*\n")
            for (opt in state.options) {
                sb.append("• *${opt.title}*: *$${String.format("%,.0f", opt.price)} MXN*\n")
                sb.append("   ${opt.description} | Entrega: ${opt.delivery}\n")
            }
            sb.append("\n*Total con gestión y logística:* *$${String.format("%,.0f", state.calculateTotalSobrePedidoFinal())} MXN*\n")
        }

        sb.append("\n📍 *Logística:* ${state.logisticsTitle}\n")
        sb.append("🛡️ *Garantía:* ${state.guaranteeTitle} (30 días por escrito)\n\n")
        sb.append("📞 *Atención WhatsApp:* 871 141 2774\n")
        sb.append("📍 *Ubicación:* Carretera a Bermejillo KM 16\n")
        sb.append("🚚 *Entrega local a domicilio:* Torreón • Gómez Palacio • Lerdo")
        return sb.toString()
    }
}
