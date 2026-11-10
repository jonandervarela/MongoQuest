
package com.mycompany.reto1_erandio_academy.servicio;

import com.mycompany.reto1_erandio_academy.dao.EmpresaDAO;
import com.mycompany.reto1_erandio_academy.modelo.Empresa;

public class EmpresaServicio {

    private final EmpresaDAO empresaDAO;

    public EmpresaServicio() {
        empresaDAO = new EmpresaDAO();
    }

    // CONSULTAR DATOS DE LA EMPRESA
    public Empresa obtenerEmpresa() {
        return empresaDAO.obtenerEmpresa();
    }

    // ACTUALIZAR DATOS DE LA EMPRESA
    public void actualizar(Empresa empresa) {

        if (empresa == null) {
            throw new IllegalArgumentException(
                    "La empresa no puede estar vacía.");
        }

        if (empresa.getNombre() == null
                || empresa.getNombre().trim().isEmpty()) {
            throw new IllegalArgumentException(
                    "El nombre de la empresa es obligatorio.");
        }

        if (empresa.getId() == null) {
            throw new IllegalArgumentException(
                    "La empresa no tiene un identificador.");
        }

        empresaDAO.actualizar(empresa);
    }

    public void cerrar() {
        empresaDAO.cerrar();
    }
}
