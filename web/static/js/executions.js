let logInterval; // Variável global para armazenar o intervalo

function viewLog(runName) {
    // Se houver um intervalo anterior, limpa-o
    if (logInterval) {
        clearInterval(logInterval);
    }
    $('#run_name').text('Process -  ' + runName + ".log");

    // Abre o modal do Bootstrap
    $('#logModal').modal('show');

    // Verifica se o runName foi fornecido
    if (runName) {
        // Chama a função para carregar o log no modal
        fetchLog(runName, '#outputAreaModal');
        logInterval = setInterval(() => fetchLog(runName, '#outputAreaModal'), 5000); // Atualiza a cada 5 segundos
    } else {
        document.querySelector('#outputAreaModal').textContent = 'Choose a process to view its log...';
    }
    $('#logModal').on('shown.bs.modal', function () {
        const outputArea = document.querySelector('#outputAreaModal');
        outputArea.scrollTop = outputArea.scrollHeight;
    });

    $('#logModal').on('hidden.bs.modal', function () {
        if (logInterval) {
            clearInterval(logInterval);
        }
    });


}

function fetchLog(runName, outputAreaId) {
    fetch(`/view-log/${runName}`)
        .then(response => response.text())
        .then(data => {
            const outputArea = document.querySelector(outputAreaId);
            outputArea.textContent = data;
            outputArea.scrollTop = outputArea.scrollHeight;
        })
        .catch(error => {
                console.error('Erro ao carregar o log:', error);
                document.querySelector(outputAreaId).textContent = 'Loading log error.';
            }
        );
}

function fetchProcessData() {
    fetch('/processes')
        .then(response => response.json())
        .then(data => {
            const tbody = document.getElementById('process-table-body');
            tbody.innerHTML = ''; // Limpa o conteúdo atual

            data.forEach(process => {
                const row = document.createElement('tr');
                row.innerHTML = `
                        <td>${process.pid}</td>
                        <td>${process.name}</td>
                        <td>${process.elapsed_time}</td>
                        <td>
                            <a href="#" onclick="killProcess(${process.pid})" title="Kill process ${process.pid}"><i class="fas fa-times"></i></a>
                            <a href="#" onclick="viewLog('${process.name}')" title="View log for ${process.name}"><i class="fas fa-eye"/></i></a>
                        </td>
                    `;
                tbody.appendChild(row);
            });
        })
        .catch(error => console.error('Erro ao buscar dados do processo:', error));
}

function killProcess(pid) {
    fetch(`/kill/${pid}`, {method: 'POST'})
        .then(response => response.json())
        .then(data => {
            if (data.success) {
                alert(`Processo ${pid} encerrado com sucesso.`);
                fetchProcessData(); // Atualiza a tabela após matar o processo
            } else {
                alert(`Erro ao encerrar o processo ${pid}: ${data.error}`);
            }
        });
}

// Atualiza a tabela a cada 5 segundos (5000 milissegundos)
setInterval(fetchProcessData, 5000);

// Busca os dados assim que a página é carregada
window.onload = fetchProcessData;