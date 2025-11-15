let index = typeof detallesSize !== "undefined" ? detallesSize : 0;

const tbody = document.getElementById('body-detalles');
const btnAgregar = document.getElementById('agregarFila');

function crearSelectProducto(nameAttr) {
  const sel = document.createElement('select');
  sel.className = 'form-select';
  sel.name = nameAttr;
  sel.required = true;

  const opt0 = document.createElement('option');
  opt0.value = '';
  opt0.textContent = '-- Selecciona producto --';
  sel.appendChild(opt0);

  (Array.isArray(productosThymeleaf) ? productosThymeleaf : []).forEach(p => {
    const opt = document.createElement('option');
    opt.value = p.id_producto;
    opt.textContent = p.nombre;
    sel.appendChild(opt);
  });

  return sel;
}

function crearInputCantidad(nameAttr) {
  const inp = document.createElement('input');
  inp.type = 'number';
  inp.className = 'form-control text-center';
  inp.min = '1';
  inp.value = '1';
  inp.name = nameAttr;
  inp.required = true;
  return inp;
}

function eliminarFila(btn) {
  btn.closest('tr').remove();
}

function agregarFila() {
  const tr = document.createElement('tr');

  const tdProd = document.createElement('td');
  tdProd.appendChild(crearSelectProducto(`detalles[${index}].producto.id_producto`));

  const tdCant = document.createElement('td');
  tdCant.appendChild(crearInputCantidad(`detalles[${index}].cantidad`));

  const tdAcc = document.createElement('td');
  const btnEliminar = document.createElement('button');
  btnEliminar.type = 'button';
  btnEliminar.className = 'btn btn-danger btn-sm';
  btnEliminar.textContent = '✕';
  btnEliminar.addEventListener('click', () => tr.remove());
  tdAcc.appendChild(btnEliminar);

  tr.appendChild(tdProd);
  tr.appendChild(tdCant);
  tr.appendChild(tdAcc);

  tbody.appendChild(tr);
  index++;
}

document.addEventListener('DOMContentLoaded', () => {
  if (tbody && btnAgregar) {
    btnAgregar.addEventListener('click', agregarFila);
  }
});
