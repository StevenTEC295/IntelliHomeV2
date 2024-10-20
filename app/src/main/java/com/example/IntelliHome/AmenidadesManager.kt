package com.example.IntelliHome

import android.content.Context
import android.widget.CheckBox
import android.widget.Toast
import com.example.intellihome.R

//ESTE ES UN CLASE SOPORTE PARA NO HACER TANTA LOGICA EN EL HostView
class AmenidadesManager(private val context: Context) {
    private val amenidadesSeleccionadas = mutableListOf<String>()

    // Función para configurar los listeners en cada CheckBox
    fun configurarListeners(
        checkBoxCocina: CheckBox, checkBoxAC: CheckBox, checkBoxCalefaccion: CheckBox,
        checkBoxWifi: CheckBox, checkBoxTv: CheckBox, checkBoxLavadora: CheckBox,
        checkBoxPiscina: CheckBox, checkBoxJardin: CheckBox, checkBoxBarbacoa: CheckBox,
        checkBoxTerraza: CheckBox, checkBoxGym: CheckBox, checkBoxGaraje: CheckBox,
        checkBoxSeguridad: CheckBox, checkBoxHabitaciones: CheckBox, checkBoxMuebles: CheckBox,
        checkBoxMicro: CheckBox, checkBoxLavajillas: CheckBox, checkBoxCafetera: CheckBox,
        checkBoxRopa: CheckBox, checkBoxComunes: CheckBox, checkBoxCamas: CheckBox,
        checkBoxLimpieza: CheckBox, checkBoxTransportePublico: CheckBox, checkBoxCercania: CheckBox,
        checkBoxRadiacion: CheckBox, checkBoxEscritorio: CheckBox, checkBoxEntretenimiento: CheckBox,
        checkBoxChimenea: CheckBox, checkBoxInternetAlta: CheckBox,checkBoxMascota:CheckBox ) {
        // Listeners para cada amenidad
        checkBoxCocina.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.cocina_equipada))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.cocina_equipada))
            }
        }

        checkBoxAC.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Aire_acondicionado))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Aire_acondicionado))
            }
        }

        checkBoxCalefaccion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.CalefacciOn))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.CalefacciOn))
            }
        }

        checkBoxWifi.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.wifi))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.wifi))
            }
        }

        checkBoxTv.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.tv_o_cable))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.tv_o_cable))
            }
        }

        checkBoxLavadora.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Lavadora_y_secadora))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Lavadora_y_secadora))
            }
        }

        checkBoxPiscina.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Piscina))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Piscina))
            }
        }

        checkBoxJardin.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Jardín_o_patio))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Jardín_o_patio))
            }
        }

        checkBoxBarbacoa.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Barbacoa_o_parrilla))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Barbacoa_o_parrilla))
            }
        }

        checkBoxTerraza.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Terraza_o_balcón))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Terraza_o_balcón))
            }
        }

        checkBoxGym.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Gimnasio_en_casa))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Gimnasio_en_casa))
            }
        }

        checkBoxGaraje.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Garaje))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Garaje))
            }
        }

        checkBoxSeguridad.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.Sistema_de_seguridad))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.Sistema_de_seguridad))
            }
        }

        checkBoxHabitaciones.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.habitaciones_con_bano_en_suite))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.habitaciones_con_bano_en_suite))
            }
        }

        checkBoxMuebles.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.muebles_de_exterior))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.muebles_de_exterior))
            }
        }

        checkBoxMicro.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.microondas))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.microondas))
            }
        }

        checkBoxLavajillas.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.lavavajillas))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.lavavajillas))
            }
        }

        checkBoxCafetera.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.cafetera))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.cafetera))
            }
        }

        checkBoxRopa.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.ropa_de_cama_y_toallas_incluidas))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.ropa_de_cama_y_toallas_incluidas))
            }
        }

        checkBoxComunes.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.acceso_a_areas_comunes))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.acceso_a_areas_comunes))
            }
        }

        checkBoxCamas.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.camas_adicionales_o_sofa_cama))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.camas_adicionales_o_sofa_cama))
            }
        }

        checkBoxLimpieza.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.servicios_de_limpieza_opcionales))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.servicios_de_limpieza_opcionales))
            }
        }

        checkBoxTransportePublico.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.acceso_a_transporte_publico_cercano))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.acceso_a_transporte_publico_cercano))
            }
        }

        checkBoxCercania.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.cercania_a_tiendas_y_restaurantes))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.cercania_a_tiendas_y_restaurantes))
            }
        }

        checkBoxRadiacion.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.sistema_de_calefaccion_por_suelo_radiante))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.sistema_de_calefaccion_por_suelo_radiante))
            }
        }

        checkBoxEscritorio.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.escritorio_o_area_de_trabajo))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.escritorio_o_area_de_trabajo))
            }
        }

        checkBoxEntretenimiento.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.sistemas_de_entretenimiento))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.sistemas_de_entretenimiento))
            }
        }

        checkBoxChimenea.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.chimenea))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.chimenea))
            }
        }

        checkBoxInternetAlta.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.acceso_a_internet_de_alta_velocidad))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.acceso_a_internet_de_alta_velocidad))
            }
        }

        checkBoxMascota.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                amenidadesSeleccionadas.add(context.getString(R.string.aceptan_mascotas))
            } else {
                amenidadesSeleccionadas.remove(context.getString(R.string.aceptan_mascotas))
            }
        }
    }

    // Función para obtener la lista de amenidades seleccionadas
    fun obtenerAmenidadesSeleccionadas(): List<String> {
        return amenidadesSeleccionadas
    }

    // Ejemplo de cómo mostrar las amenidades seleccionadas
    fun mostrarAmenidadesSeleccionadas() {
        Toast.makeText(context, "Amenidades: $amenidadesSeleccionadas", Toast.LENGTH_SHORT).show()
    }
}