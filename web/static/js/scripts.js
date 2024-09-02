$(document).ready(function () {
  $("#dataTable").DataTable();
});

window.onload = function() {
  // Hide loader overlay once the page is fully loaded
  document.getElementById('loader-overlay').style.display = 'none';
};

// Show loader overlay when the page starts loading
document.getElementById('loader-overlay').style.display = 'flex';

document.addEventListener("DOMContentLoaded", function () {
  const buttons = document.querySelectorAll(".show-more-btn");
  /*

  */
  buttons.forEach((button) => {
    button.addEventListener("click", function () {
      const textContainer = button.closest(".text-container");
      const fullText = textContainer.querySelector(".full-text");
      const textTruncated = textContainer.querySelector(".text-truncated");

      if (fullText.style.display === "none") {
        fullText.style.display = "inline";
        textTruncated.style.display = "none";
        button.innerHTML = '<i class="fas fa-eye-slash"></i>'; // Ícone para ocultar o texto
      } else {
        fullText.style.display = "none";
        textTruncated.style.display = "inline";
        button.innerHTML = '<i class="fas fa-eye"></i>'; // Ícone para mostrar o texto
      }
    });
  });

  // Função para formatar o texto quebrando linhas maiores que 65 caracteres
  function formatText(text) {
    const maxLength = 65;
    let formattedText = '';
    for (let i = 0; i < text.length; i += maxLength) {
      formattedText += text.slice(i, i + maxLength) + '<br>';
    }
    return formattedText;
  }

  // Aplica a formatação ao texto truncado e ao texto completo
  document.querySelectorAll('.text-container').forEach(container => {
    const fullText = container.querySelector('.full-text');
    const textTruncated = container.querySelector('.text-truncated');

    if (fullText) {
      fullText.innerHTML = formatText(fullText.innerHTML);
    }
    if (textTruncated) {
      textTruncated.innerHTML = formatText(textTruncated.innerHTML);
    }
  });
});

document.querySelector('.navbar-toggler').addEventListener('click', function() {
  const sidebar = document.getElementById('sidebar');
  const mainContent = document.getElementById('mainContent');
  if (sidebar.classList.contains('open')) {
      sidebar.classList.remove('open');
      mainContent.classList.remove('shifted');
  } else {
      sidebar.classList.add('open');
      mainContent.classList.add('shifted');
  }
});

