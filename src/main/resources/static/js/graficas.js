document.addEventListener("DOMContentLoaded", async () => {
  try {
    const [historialRes, comparativaRes] = await Promise.all([
      fetch("/api/clientesPrecio"),
      fetch("/api/competenciaComparativa")
    ]);

    const data = await historialRes.json();
    const dataCompetencia = await comparativaRes.json();

    if (data.length > 0) {
      const clientesUnicos = [...new Set(data.map(d => d.cliente))];
      const ctxEvo = document.getElementById('chartEvolucion');

      new Chart(ctxEvo, {
        type: 'line',
        data: {
          datasets: clientesUnicos.map(cliente => ({
            label: cliente,
            data: data
              .filter(d => d.cliente === cliente)
              .map(d => ({
                x: new Date(d.fecha),
                y: d.precio,
                cliente: d.cliente
              })),
            borderWidth: 2,
            tension: 0.3
          }))
        },
        options: {
          responsive: true,
          plugins: {
            title: { display: true, text: 'Precio entre clientes' },
            legend: { position: 'bottom' },
            tooltip: {
              callbacks: {
                label: ctx => {
                  const d = ctx.raw;
                  const fecha = new Date(d.x).toLocaleDateString('es-MX');
                  return `${ctx.dataset.label}: $${d.y} (${fecha})`;
                }
              }
            }
          },
          scales: {
            x: {
              type: 'time',
              time: { unit: 'day', tooltipFormat: 'dd/MM/yyyy' },
              title: { display: true, text: 'Fecha' }
            },
            y: { beginAtZero: false, title: { display: true, text: 'Precio' } }
          }
        }
      });


      ctxEvo.addEventListener("click", () => {
        window.location.href = "/graficas/detalles/clientePrecio";
      });
    }


    if (dataCompetencia.length > 0) {
      const datosCompetencia = dataCompetencia
        .filter(d => d.competencia === 1 || d.competencia === true)
        .map(d => ({
          x: new Date(d.fecha),
          y: d.precio,
          producto: d.producto,
          cliente: d.cliente
        }));

      const datosNoCompetencia = dataCompetencia
        .filter(d => d.competencia === 0 || d.competencia === false)
        .map(d => ({
          x: new Date(d.fecha),
          y: d.precio,
          producto: d.producto,
          cliente: d.cliente
        }));

      const ctxComp = document.getElementById('chartCompetencia');

      new Chart(ctxComp, {
        type: 'scatter',
        data: {
          datasets: [
            {
              label: 'Competencia',
              data: datosCompetencia,
              backgroundColor: 'rgba(255, 99, 132, 0.8)',
              borderColor: 'rgba(255, 99, 132, 1)',
              pointRadius: 2
            },
            {
              label: 'Producto de la empresa',
              data: datosNoCompetencia,
              backgroundColor: 'rgba(21, 106, 10, 0.8)',
              borderColor: 'rgba(21, 106, 10, 1)',
              pointRadius: 2
            }
          ]
        },
        options: {
          responsive: true,
          plugins: {
            title: { display: true, text: 'Comparativa de precios entre competencia' },
            legend: { position: 'bottom' },
            tooltip: {
              callbacks: {
                label: ctx => {
                  const d = ctx.raw;
                  const fecha = new Date(d.x).toLocaleDateString('es-MX');
                  return `${d.producto || ''} (${d.cliente || ''}) - $${d.y} [${fecha}]`;
                }
              }
            }
          },
          scales: {
            x: { type: 'time', time: { unit: 'day' }, title: { display: true, text: 'Fecha' } },
            y: { beginAtZero: false, title: { display: true, text: 'Precio' } }
          }
        }
      });

      ctxComp.addEventListener("click", () => {
        window.location.href = "/graficas/detalles/competenciaComparativa";
      });
    }

  } catch (err) {
    console.error("Error cargando los datos del dashboard:", err);
  }
});

(async () => {
  try {
    const res = await fetch("/api/ventasGrafica");
    const data = await res.json();

    if (!data || data.length === 0) return;

    const ctx = document.getElementById("chartVentasProducto");
    const clientes = data.map(d => d.cliente);
    const cantidades = data.map(d => d.cantidad);

    new Chart(ctx, {
      type: "bar",
      data: {
        labels: clientes,
        datasets: [{
          label: "Cantidad vendida",
          data: cantidades,
          backgroundColor: "rgba(75, 192, 192, 0.7)",
          borderColor: "rgba(75, 192, 192, 1)",
          borderWidth: 1
        }]
      },
      options: {
        responsive: true,
        plugins: {
          title: { display: true, text: "Total de Cajas vendidas" },
          legend: { display: false }
        },
        scales: {
          y: { beginAtZero: true, title: { display: true, text: "Cantidad" } },
          x: { title: { display: true, text: "Cliente" } }
        }
      }
    });

    ctx.addEventListener("click", () => {
      window.location.href = "/graficas/detalles/ventasTotal";
    });
  } catch (err) {
    console.error("Error al cargar la gráfica de producto:", err);
  }
})();
