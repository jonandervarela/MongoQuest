
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.modelo.Tarea;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.ss.util.CellRangeAddress;

public class ExportarExcel {

    public void exportarTareas(
            List<Tarea> tareas,
            List<String> columnas,
            String ruta) throws IOException {

        // CARGAR LA PLANTILLA EXCEL

        InputStream entrada = getClass().getResourceAsStream(
                "/plantillas/plantilla_tareas.xlsx"
        );

        if (entrada == null) {
            throw new IOException("No se encuentra la plantilla Excel.");
        }

        try (InputStream archivoPlantilla = entrada;
             Workbook libro = WorkbookFactory.create(archivoPlantilla)) {

            // OBTENER LA HOJA TAREAS

            Sheet hoja = libro.getSheet("Tareas");

            if (hoja == null) {
                hoja = libro.createSheet("Tareas");
            }

            // ELIMINAR DATOS ANTERIORES

            for (int i = hoja.getLastRowNum(); i >= 0; i--) {

                Row filaAnterior = hoja.getRow(i);

                if (filaAnterior != null) {
                    hoja.removeRow(filaAnterior);
                }
            }

            // ESTILO DE LA CABECERA

            CellStyle estiloCabecera = libro.createCellStyle();

            Font fuenteCabecera = libro.createFont();

            fuenteCabecera.setBold(true);

            fuenteCabecera.setColor(
                    IndexedColors.WHITE.getIndex());

            fuenteCabecera.setFontHeightInPoints((short) 11);

            estiloCabecera.setFont(fuenteCabecera);

            estiloCabecera.setFillForegroundColor(
                    IndexedColors.DARK_BLUE.getIndex());

            estiloCabecera.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);

            estiloCabecera.setAlignment(
                    HorizontalAlignment.CENTER);

            estiloCabecera.setVerticalAlignment(
                    VerticalAlignment.CENTER);

            estiloCabecera.setBorderBottom(
                    BorderStyle.MEDIUM);

            // ESTILO DE LAS FILAS

            CellStyle estiloFila = libro.createCellStyle();

            estiloFila.setVerticalAlignment(
                    VerticalAlignment.CENTER);

            estiloFila.setBorderBottom(BorderStyle.THIN);

            estiloFila.setBorderTop(BorderStyle.THIN);

            estiloFila.setBorderLeft(BorderStyle.THIN);

            estiloFila.setBorderRight(BorderStyle.THIN);

            // ESTILO DE LAS FILAS ALTERNAS

            CellStyle estiloFilaAlterna = libro.createCellStyle();

            estiloFilaAlterna.cloneStyleFrom(estiloFila);

            estiloFilaAlterna.setFillForegroundColor(
                    IndexedColors.LIGHT_BLUE.getIndex());

            estiloFilaAlterna.setFillPattern(
                    FillPatternType.SOLID_FOREGROUND);

            // CREAR CABECERA

            Row cabecera = hoja.createRow(0);

            cabecera.setHeightInPoints(25);

            int columna = 0;

            for (String campo : columnas) {

                Cell celda = cabecera.createCell(columna++);

                celda.setCellValue(campo);

                celda.setCellStyle(estiloCabecera);
            }

            // INSERTAR DATOS

            int fila = 1;

            for (Tarea tarea : tareas) {

                Row row = hoja.createRow(fila);

                columna = 0;

                CellStyle estiloActual;

                if (fila % 2 == 0) {
                    estiloActual = estiloFilaAlterna;
                } else {
                    estiloActual = estiloFila;
                }

                for (String campo : columnas) {

                    String valor = "";

                    switch (campo) {

                        case "ID":
                            valor = tarea.getId() != null
                                    ? tarea.getId().toString() : "";
                            break;

                        case "Título":
                            valor = tarea.getTitulo() != null
                                    ? tarea.getTitulo() : "";
                            break;

                        case "Profesor ID":
                            valor = tarea.getProfesorId() != null
                                    ? tarea.getProfesorId().toString() : "";
                            break;

                        case "Curso ID":
                            valor = tarea.getCursoId() != null
                                    ? tarea.getCursoId().toString() : "";
                            break;

                        case "Fecha límite":
                            valor = tarea.getFechaLimite() != null
                                    ? tarea.getFechaLimite() : "";
                            break;
                    }

                    Cell celda = row.createCell(columna++);

                    celda.setCellValue(valor);

                    celda.setCellStyle(estiloActual);
                }

                fila++;
            }

            // AJUSTAR COLUMNAS

            for (int i = 0; i < columnas.size(); i++) {

                hoja.autoSizeColumn(i);

                // DAR MÁS ESPACIO A FECHA LÍMITE

                if (columnas.get(i).equals("Fecha límite")) {

                    hoja.setColumnWidth(i, 4500);

                } else if (hoja.getColumnWidth(i) < 3000) {

                    hoja.setColumnWidth(i, 3000);
                }
            }

            // ACTIVAR FILTROS

            if (!columnas.isEmpty()) {

                hoja.setAutoFilter(
                        new CellRangeAddress(
                                0,
                                Math.max(0, fila - 1),
                                0,
                                columnas.size() - 1
                        )
                );
            }

            // FIJAR LA CABECERA

            hoja.createFreezePane(0, 1);

            // OCULTAR CUADRÍCULA

            hoja.setDisplayGridlines(false);

            // CONFIGURAR IMPRESIÓN

            hoja.getPrintSetup().setLandscape(true);

            hoja.setFitToPage(true);

            hoja.getPrintSetup().setFitWidth((short) 1);

            hoja.getPrintSetup().setFitHeight((short) 0);

            // GUARDAR ARCHIVO EXCEL

            try (FileOutputStream archivo =
                    new FileOutputStream(ruta)) {

                libro.write(archivo);
            }
        }
    }
}
