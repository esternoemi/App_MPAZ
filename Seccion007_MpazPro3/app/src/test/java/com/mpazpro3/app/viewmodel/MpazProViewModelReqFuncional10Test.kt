package com.mpazpro3.app.viewmodel

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MpazProViewModelReqFuncional10Test {

    @Test
    fun respuestaDePracticaSeGuardaYSeFiltraPorEstudianteYContenido() {
        val viewModel = MpazProViewModel()
        val desafio = viewModel.desafiosDe("C2").first { it.id == "A1" }

        assertEquals(true, viewModel.responderDesafio("EST-001", desafio, 0))
        assertEquals(false, viewModel.responderDesafio("EST-001", desafio, 1))

        val respuestas = viewModel.respuestasDe("EST-001", "C2")
        assertEquals(1, respuestas.size)
        assertEquals(1, respuestas.single().alternativaElegida)
        assertFalse(respuestas.single().esCorrecta)
        assertEquals(0, respuestas.single().numeroIntento)
        assertTrue(viewModel.respuestasDe("EST-002", "C2").isEmpty())
        assertTrue(viewModel.respuestasDe("EST-001", "C1").isEmpty())
    }

    @Test
    fun respuestasDeEvaluacionSeGuardanConCorreccionYNumeroDeIntento() {
        val viewModel = MpazProViewModel()
        val preguntas = viewModel.evaluacionFinalDe("C2")

        assertEquals(listOf("E1", "E2"), preguntas.map { it.id })

        val respuestasPrimerIntento = mutableMapOf<String, Int>()
        respuestasPrimerIntento["E1"] = 0
        respuestasPrimerIntento["E2"] = 1
        assertTrue(
            viewModel.enviarEvaluacion(
                "EST-001",
                "C2",
                respuestasPrimerIntento
            )
        )

        val respuestasSegundoIntento = mutableMapOf<String, Int>()
        respuestasSegundoIntento["E1"] = 1
        respuestasSegundoIntento["E2"] = 0
        assertTrue(
            viewModel.enviarEvaluacion(
                "EST-001",
                "C2",
                respuestasSegundoIntento
            )
        )

        val respuestas = viewModel.respuestasDe("EST-001", "C2")
            .filter { it.numeroIntento > 0 }

        assertEquals(listOf(1, 1, 2, 2), respuestas.map { it.numeroIntento })
        assertEquals(listOf(true, false, false, true), respuestas.map { it.esCorrecta })
    }

    @Test
    fun envioDeEvaluacionInvalidoSeRechazaYNoSeGuarda() {
        val viewModel = MpazProViewModel()
        val respuestasInvalidas = mutableMapOf<String, Int>()
        respuestasInvalidas["E1"] = 99

        assertFalse(
            viewModel.enviarEvaluacion(
                "EST-001",
                "C2",
                respuestasInvalidas
            )
        )
        assertTrue(viewModel.respuestasDe("EST-001", "C2").isEmpty())
    }
}