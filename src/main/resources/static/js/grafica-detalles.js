document.addEventListener("DOMContentLoaded", () => {

    let chart = null;

    const selectProducto = document.getElementById("filtroProducto");
    const selectAnio = document.getElementById("filtroAnio");
    const selectMes = document.getElementById("filtroMes");
    const selectTexto = document.getElementById("filtroTexto");

    const tipo = document.querySelector("canvas")?.id;

    const endpoint =
        tipo === "graficaClientes"
            ? "/api/detallada/clientesPrecio"
        : tipo === "graficaCompetencia"
            ? "/api/detallada/comparativaProductos"
        : tipo === "graficaVentasProduct"
                ? "/api/detallada/ventasPorCliente"
            : "/api/detallada/ventas";

    async function cargarGrafica() {

       let params;

       if (tipo === "graficaVentasProduct") {

           const normalizar = v =>
               (v === undefined || v === null || v === "" || v === "null" || v === "Todos")
                   ? ""
                   : v;

           params = new URLSearchParams({
               producto: normalizar(selectProducto?.value),
               anio: normalizar(selectAnio?.value),
               mes: normalizar(selectMes?.value),
               keyword: normalizar(selectTexto?.value?.trim())
           });

       } else {

           // Mantener comportamiento original para las otras gráficas
           params = new URLSearchParams({
               producto: selectProducto && selectProducto.value !== "Todos" ? selectProducto.value : null,
               anio    : selectAnio && selectAnio.value !== "Todos" ? selectAnio.value : null,
               mes     : selectMes && selectMes.value !== "Todos" ? selectMes.value : null,
               keyword : (selectTexto && selectTexto.value.trim() !== "") ? selectTexto.value.trim() : null
           });

       }
        console.log("Enviando params:", params.toString());

        const res = await fetch(`${endpoint}?${params.toString()}`);
        const data = await res.json();

        console.log("Datos recibidos:", data);

        const ctx = document.getElementById(tipo);
        if (!ctx) return;

        if (chart) chart.destroy();


        // ------------------ GRAFICA CLIENTES ------------------
        if (tipo === "graficaClientes") {

            if (!data || data.length === 0) {
                chart = dibujarGraficaVacia(ctx, "Sin datos de Clientes");
                return;
            }

            const grupos = {};

            data.forEach(([fecha, cliente, precio]) => {
                if (!grupos[cliente]) grupos[cliente] = [];
                grupos[cliente].push({ fecha, precio });
            });

            const datasets = Object.entries(grupos).map(([cliente, valores], i) => ({
                label: cliente,
                data: valores.map(v => v.precio),
                borderWidth: 2,
                tension: 0.2,
                fill: false,
                pointRadius: 6,
                borderColor: `hsl(${i * 70}, 70%, 50%)`,
                backgroundColor: `hsl(${i * 70}, 70%, 50%)`
            }));

            chart = new Chart(ctx, {
                type: "line",
                data: {
                    labels: [...new Set(data.map(d => new Date(d[0]).toLocaleDateString()))],
                    datasets
                }
            });

            return;
        }


        // ------------------ GRAFICA COMPETENCIA ------------------
        if (tipo === "graficaCompetencia") {

            if (!data || data.length === 0) {
                chart = dibujarGraficaVacia(ctx, "Sin datos");
                return;
            }

            const fechas = [...new Set(data.map(d => new Date(d[0]).toLocaleDateString()))];

            const empresa = fechas.map(f =>
                data.find(d => d[3] === false && new Date(d[0]).toLocaleDateString() === f)?.[2] ?? null
            );

            const competencia = fechas.map(f =>
                data.find(d => d[3] === true && new Date(d[0]).toLocaleDateString() === f)?.[2] ?? null
            );

            chart = new Chart(ctx, {
                type: "line",
                data: {
                    labels: fechas,
                    datasets: [
                        {
                            label: "Producto de la empresa",
                            data: empresa,
                            borderColor: "#2ECC71",
                            backgroundColor: "#2ECC71",
                            pointRadius: 6,
                            borderWidth: 2
                        },
                        {
                            label: "Competencia",
                            data: competencia,
                            borderColor: "#E74C3C",
                            backgroundColor: "#E74C3C",
                            pointRadius: 6,
                            borderWidth: 2
                        }
                    ]
                }
            });

            return;
        }

        if (tipo === "graficaVentasProduct") {

              if (!data || data.length === 0) {
                  chart = dibujarGraficaVacia(ctx, "Sin datos de ventas");
                  return;;
              }

              console.log("DATA VENTAS:", data);

              // Estructura esperada: [cliente, cantidad]
              const labels = data.map(d => d[0]);
              const valores = data.map(d => d[1]);

              chart = new Chart(ctx, {
                  type: "bar",
                  data: {
                      labels,
                      datasets: [{
                          label: "Total de cajas vendidas",
                          data: valores,
                          backgroundColor: "#76D7C4",
                          borderColor: "#48C9B0",
                          borderWidth: 2,
                          borderRadius: 12,
                          hoverBackgroundColor: "#1ABC9C"
                      }]
                  },
                  options: {
                      responsive: true,
                      plugins: {
                          tooltip: {
                              callbacks: {
                                  label: (ctx) => `${ctx.label}: ${ctx.raw} cajas`
                              }
                          },
                          legend: {
                              display: true
                          }
                      },
                      scales: {
                          y: {
                              beginAtZero: true,
                              title: {
                                  display: true,
                                  text: "Cantidad"
                              }
                          },
                          x: {
                              title: {
                                  display: true,
                                  text: "Cliente"
                              }
                          }
                      }
                  }
              });

              return;
          }
     }

    function dibujarGraficaVacia(ctx, label) {
        if (chart) chart.destroy(); // aseguramos limpieza

        return new Chart(ctx, {
            type: "bar",
            data: {
                labels: [" "],
                datasets: [{
                    label,
                    data: [0],
                    backgroundColor: "#d5d8dc",
                    borderColor: "#b2babb",
                    borderWidth: 1
                }]
            },
            options: {
                plugins: {
                    legend: {
                        display: true
                    },
                    tooltip: {
                        enabled: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true
                    }
                }
            }
        });
    }

    [selectProducto, selectAnio, selectMes, selectTexto].forEach(f => {
        if (f) f.addEventListener("change", cargarGrafica);
    });

    document.getElementById("btnFiltrar")?.addEventListener("click", cargarGrafica);

    cargarGrafica();

    const btnPDF = document.getElementById("btnPDF");

    if (btnPDF) {
        btnPDF.addEventListener("click", () => {
            if (!chart) {
                alert("Primero genera la gráfica antes de exportar.");
                return;
            }

            const { jsPDF } = window.jspdf;
            const pdf = new jsPDF();

            const canvas = document.getElementById(tipo);
            if (!canvas) {
                alert("No se encontró el canvas de la gráfica.");
                return;
            }

            const imgData = canvas.toDataURL("image/png", 1.0);

            const titulo =
              tipo === "graficaClientes" ? "Precios por cliente" :
              tipo === "graficaCompetencia" ? "Comparativa competencia vs marca" :
              "Ventas por cliente";

            pdf.text(titulo, 10, 10);
            pdf.addImage(imgData, "PNG", 10, 20, 180, 100);

            const nombre = tipo ? `reporte-${tipo}.pdf` : "reporte-grafica.pdf";
            pdf.save(nombre);
        });
    }
});
