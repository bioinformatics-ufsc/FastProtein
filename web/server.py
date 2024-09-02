from flask import Flask, request, jsonify, render_template, session, make_response, send_file, flash
from flask import Blueprint, send_from_directory, redirect, url_for
from functools import wraps
from werkzeug.utils import secure_filename
import os
import zipfile
import json
import tempfile
import re
import subprocess
import shutil
import time
import os.path
import time
import sys
from flask import Flask, request, render_template, send_from_directory, redirect, url_for, Blueprint
from datetime import datetime
from Bio import SeqIO
import re
import csv
from io import StringIO, BytesIO
import pandas as pd
import textwrap
from datetime import timedelta
import random
import base64
import threading

app = Flask(__name__)
FLASK_HOME = os.getenv('FLASK_HOME')

if FLASK_HOME is None:
    FLASK_HOME = os.path.abspath(os.path.dirname(__file__))
DB_PATH = os.getenv('DATABASE_HOME')

if DB_PATH is None:
    # Obtenha o caminho raiz da aplicação Flask
    BASE_DIR = os.path.abspath(os.path.dirname(__file__))
    # Define DB_PATH como o diretório 'db' na raiz da aplicação
    DB_PATH = os.path.join(BASE_DIR, 'db')

OUTPUT_PATH = FLASK_HOME + '/runs'

os.makedirs(DB_PATH, exist_ok=True)
os.makedirs(OUTPUT_PATH, exist_ok=True)

FLASK_DEBUG = os.getenv('FLASK_DEBUG')

# Database controls
ALLOWED_EXTENSIONS = {'.fa', '.fas', '.fasta', '.faa', '.dmnd'}
# os.makedirs(output_folder, exist_ok=True)

# app.static_folder=flask_home+'/static'
errors = []

filtered_lines = [
    '503 Thu Aug 22 16:37:45 2024 /bin/bash /usr/local/bin/fastprotein -i /fastprotein/runs/testefastprotein-1724344665.fasta -s animal -zip -o /fastprotein/runs/testefastprotein-1724344665_results -log ALL',
    '608 Thu Aug 22 16:47:44 2024 /bin/bash /usr/local/bin/fastprotein -i /fastprotein/runs/fastprotein2-1724345264.fasta -s animal -zip -o /fastprotein/runs/fastprotein2-1724345264_results -log ALL']

static_bp = Blueprint('runs', __name__,
                      static_folder='/runs',
                      static_url_path='/runs')

app.register_blueprint(static_bp, url_prefix='/')

app.secret_key = os.urandom(24)


@app.route('/static/<path:filename>')
def serve_static(filename):
    return send_from_directory(app.static_folder, filename)


@app.route('/runs/<path:filename>')
def serve_runs(filename):
    return send_from_directory(static_bp.static_folder, filename)


@app.route('/')
def index():
    session['type'] = 'new'
    auto_login()
    # render the template with the file list
    return render_template('index.html', dbs=load_db_list(), session=session)


def load_db_list():
    dbs = [file for file in os.listdir(DB_PATH) if file.endswith('.dmnd')]

    db_list = []

    for db_name in dbs:
        if db_name.endswith(".dmnd"):
            db_path = os.path.join(DB_PATH, db_name)
            db_stat = os.stat(db_path)
            formatted_date = datetime.fromtimestamp(db_stat.st_ctime).strftime('%Y-%m-%d %H:%M')
            file_size_mb = os.path.getsize(db_path) / (1024 * 1024)

            db_dict = {
                'name': db_name,
                'date': formatted_date,
                'path': db_path,
                'size_mb': f"{file_size_mb:.2f} MB",
            }
            db_list.append(db_dict)

    db_list.sort(key=lambda x: x['name'], reverse=True)
    return db_list


@app.route('/run', methods=['POST'])
def run():
    session['type'] = 'new'
    inputDB = ''
    inputFasta = ''
    db_list = load_db_list()
    auto_login()
    name_run = request.form.get('run-name')
    user = request.form.get('user')
    if name_run is None:
        name_run = 'fastprotein'

    timestamp = str(int(time.time()))
    run_id = f'{user}-{name_run}-{timestamp}'
    print('run-id', run_id)
    params = []

    file = request.files['fasta']
    form = request.form
    print('FORMULARIO', request.form)
    if file.filename != '':
        inputFasta = OUTPUT_PATH + '/' + run_id + '.fasta'
        file.save(inputFasta)
        print('File saved: ' + inputFasta)

        if is_fasta_file(inputFasta):
            params.extend(['-i', inputFasta])
        else:
            error = 'Input file is not a FASTA file. Please check the file and try again.'
            os.remove(inputFasta)
            flash(error, 'danger')
            return render_template('index.html', session=session, dbs=db_list)

    else:
        flash('Error: Inform a FASTA file', 'danger')
        return render_template('index.html', session=session, dbs=db_list)

    subcell = request.form.get('subcell')
    if subcell:
        print("subcell=" + subcell)
        params.extend(['-s', subcell])

    interpro = request.form.get('interpro')
    if interpro:
        params.extend(['--interpro'])

    run_similarity = request.form.get('searchSimilarity')
    if run_similarity:
        selectDB = request.form.get('selectDB')
        if selectDB == 'fasta_diamond':
            file_db = request.files['dbSearch']
            file_ext = os.path.splitext(file_db.filename)[1].lower()
            if file_db and file_ext in ALLOWED_EXTENSIONS:
                filename = file_db.filename
                inputDB = OUTPUT_PATH + '/' + run_id + "-db-" + filename
                local_search = True
                file_db.save(inputDB)
                params.extend(['-db', inputDB])
            else:
                flash('Inform a FASTA or Diamond DB to continue', 'error')
                if os.path.exists(inputFasta):
                    os.remove(inputFasta)
                return render_template('index.html', session=session, dbs=db_list)
        elif selectDB == 'database':
            filename = request.form.get('selectDBFile')
            if filename:
                inputDB = os.path.join(DB_PATH, filename)
                params.extend(['-db', inputDB])
        else:
            flash('Inform a FASTA or Diamond DB to continue', 'error')
            return render_template('index.html', session=session, dbs=db_list)
    params.extend(['-zip'])
    folder = OUTPUT_PATH + "/" + run_id + '_results'
    params.extend(['-o', folder])
    params.extend(['-log', 'ALL'])
    params.extend(['>', f"{OUTPUT_PATH}/{run_id}.log"])

    # subprocess.run(['fastprotein'] + params)
    # print('Result files: ' +  folder +".zip")
    # print('Removing input files')
    # subprocess.run(['rm', inputFasta])
    # if local_search:
    #     subprocess.run(['rm', inputDB])
    # subprocess.run(['rm', '-r', folder])

    # if os.path.exists(inputFasta):
    #     os.remove(inputFasta)
    # if os.path.exists(inputDB):
    #     os.remove(inputDB)
    #thread = threading.Thread(target=run_fastprotein_task, args=(params, folder, inputFasta, inputDB, local_search))
    #thread.start()

    filtered_lines.append(generate_line(params))
    flash('Your process is running under id ' + run_id, 'success')
    return render_template('index.html', session=session, dbs=db_list)


def run_fastprotein_task(params, folder, inputFasta, inputDB, local_search):

    subprocess.run(['fastprotein'] + params)
    print('Result files: ' + folder + ".zip")
    print('Removing input files')
    subprocess.run(['rm', inputFasta])
    if local_search:
        subprocess.run(['rm', inputDB])
    subprocess.run(['rm', '-r', folder])


def generate_line(params):
    # Gerar um ID aleatório acima de 700
    id = random.randint(701, 9999)

    # Obter a data e hora atual no formato especificado
    current_time = time.strftime("%a %b %d %H:%M:%S %Y")

    # Concatenar os parâmetros
    parametros = ' '.join(params)

    # Montar a linha final
    line = f"{id} {current_time} /bin/bash /usr/local/bin/fastprotein {parametros}"

    return line


@app.route('/remove_file', methods=['GET'])
def remove_file():
    session['type'] = 'view'
    file_name = request.args.get('file')
    if not file_name:
        return render_template('view.html', session=session)

    print('Removing file:', file_name)

    # Caminho completo do arquivo e do diretório
    file_path = os.path.join(OUTPUT_PATH, file_name)
    base_name = os.path.splitext(file_name)[0]
    directory_path = os.path.join(OUTPUT_PATH, base_name)

    # Remove o arquivo, se existir
    if os.path.isfile(file_path):
        os.remove(file_path)

    # Remove o diretório, se existir
    if os.path.isdir(directory_path):
        shutil.rmtree(directory_path)

    return render_template('view.html', session=session)


def is_fasta_file(filepath):
    try:
        # try parsing the file using SeqIO.parse()
        records = SeqIO.parse(filepath, 'fasta')
        print(records)
        count = 0
        # loop through the records in the iterator
        for record in records:
            count = count + 1
        print(f"Proteins in FASTA {count}")

        print(filepath + ' is a valid FASTA')
        # if parsing succeeds, the file is in FASTA format
        return True
    except ValueError:
        print(filepath + ' is a invalid FASTA')
        # if parsing fails, the file is not in FASTA format
        return False


@app.route('/view', methods=['GET'])
def view():
    session['type'] = 'view'
    files = os.listdir(OUTPUT_PATH)
    file_list = []
    for file_name in files:
        if file_name.endswith(".zip"):
            file_path = os.path.join(OUTPUT_PATH, file_name)
            file_stat = os.stat(file_path)
            file_dict = {
                'name': file_name,
                'seconds': file_stat.st_mtime,
                'date': datetime.fromtimestamp(file_stat.st_mtime).strftime("%A, %B %d, %Y %I:%M:%S"),
                'path': file_name,
            }
            file_list.append(file_dict)
    file_list.sort(key=lambda x: x['seconds'], reverse=True)

    proteins = []
    # file = os.path.join(output_folder, filename)
    file = request.args.get('file')
    if file:
        file = os.path.join(OUTPUT_PATH, file)
        if not os.path.isfile(file):
            return jsonify({"error": "No selected file"}), 400

        if file.endswith('.zip'):
            destination_file_path = os.path.join(OUTPUT_PATH, os.path.basename(file))
            print('destination_file_path', destination_file_path)

            base_name = os.path.splitext(os.path.basename(destination_file_path))[0]

            if not os.path.exists(base_name):
                print('UNZIP FILE', destination_file_path)
                base_folder = unzip_file(destination_file_path)

            tsv_path = os.path.join(base_folder, 'output.tsv')
            if not os.path.exists(tsv_path):
                return jsonify({"error": "output.tsv not found in the zip file"}), 400

            df = pd.read_csv(tsv_path, delimiter='\t')
            df.columns = df.columns.str.lower()
            print(df.columns)
            df = df.fillna('-')
            df['header'] = df['header'].apply(extract_name)
            df['local_alignment_description'] = df['local_alignment_description'].apply(extract_name)
            json_result = df.to_json(orient='records', indent=4)
            proteins = json.loads(json_result)

    return render_template('view.html', files=file_list, proteins=proteins, session=session)


def unzip_file(zip_file_path):
    base_name = os.path.splitext(os.path.basename(zip_file_path))[0]
    destination_folder = os.path.dirname(zip_file_path)  # Diretório onde o arquivo ZIP está localizado
    extraction_folder = destination_folder

    # Cria o diretório de extração se não existir
    if not os.path.exists(extraction_folder):
        os.makedirs(extraction_folder)

    # Descompacta o arquivo ZIP
    with zipfile.ZipFile(zip_file_path, 'r') as zip_ref:
        zip_ref.extractall(extraction_folder)

    return os.path.join(destination_folder, base_name)


def extract_name(text):
    # Check if the string contains 'OS='
    if 'OS=' in text:
        # Regular expression to capture text between the first space and 'OS='
        match = re.search(r'\s(.*?)\sOS=', text)
        if match:
            return match.group(1)
    # If 'OS=' is not present, check if the string contains '['
    elif '[' in text:
        # Regular expression to capture text between the first space and the character '['
        match = re.search(r'\s(.*?)\s\[', text)
        if match:
            return match.group(1)
    # If neither condition is satisfied, return everything after the first space
    else:
        match = re.search(r'\s(.*)', text)
        if match:
            return match.group(1)
    # Return the entire string if no space is found
    return text


##Process management
def get_process_info():
    try:
        # result = subprocess.run(
        #     'ps -eo pid,lstart,command | grep "/bin/bash /usr/local/bin/fastprotein -i /fastprotein/runs"',
        #     shell=True,
        #     stdout=subprocess.PIPE,
        #     text=True
        # )
        #
        # process_list = result.stdout
        #
        # filtered_lines = [line for line in process_list.splitlines() if '/bin/bash /usr/local/bin/fastprotein -i /fastprotein/runs' in line]
        print(filtered_lines)
        process_info = []
        for line in filtered_lines:
            # print('--------------')
            parts = line.split(" ")  # Divida em 3 partes: PID, tempo de início, e comando
            if len(parts) > 2:
                pid = parts[0]
                start_time_str = ' '.join(parts[1:6])  # Captura o tempo de início
                cmd = (' '.join(parts[6:len(parts)])).replace('_results', '').strip()

                # print('id', pid)
                # print('time', start_time_str)
                # print('cmd', cmd)
                if cmd.startswith('/bin/bash /usr/local/bin/fastprotein'):

                    # print(pid)
                    # print(start_time_str)
                    # print(cmd)
                    # print('--------------')

                    # Converte o tempo de início para um objeto datetime
                    try:
                        start_time = datetime.strptime(start_time_str, '%a %b %d %H:%M:%S %Y')
                        current_time = datetime.now()
                        elapsed_time = current_time - start_time

                        # Formata o tempo de execução como uma string
                        elapsed_str = str(elapsed_time).split('.')[0]  # Remove fração de segundos
                    except ValueError:
                        elapsed_str = 'N/A'

                    # Extraia apenas o nome do processo
                    process_name = cmd.split('/')[-1]  # Isso extrai apenas o nome do comando

                    process_info.append({'pid': pid, 'name': process_name, 'elapsed_time': elapsed_str})

        return process_info

    except Exception as e:
        print(f"Erro ao obter informações do processo: {e}")
        return []


@app.route('/processes')
def processes():
    return jsonify(get_process_info())


@app.route('/kill/<int:pid>', methods=['POST'])
def kill_process(pid):
    try:
        os.kill(pid, 9)  # Envia sinal de matar (SIGKILL) para o processo

        return jsonify({"success": True})
    except Exception as e:
        return jsonify({"success": False, "error": str(e)})


@app.route('/view-log/<run_name>')
def view_log(run_name):
    try:
        log_file_path = f"{OUTPUT_PATH}/{run_name}.log"
        with open(log_file_path, 'r') as file:
            log_content = file.read()
        return log_content
    except Exception as e:
        return f"Erro ao abrir o log: {str(e)}", 500


##End process management

@app.route('/teste')
def teste():
    return render_template('teste.html')


@app.route('/download', methods=['POST'])
def download():
    filtered_ids = request.form.get('filteredIds')
    uploaded_file = request.form.get('file')
    file_extension = request.form.get('ext')

    filtered_ids_list = filtered_ids.split(",") if filtered_ids else []

    print('ids', filtered_ids_list)
    print('file', uploaded_file)
    print('upload folder', OUTPUT_PATH)
    print('file sem zip', os.path.splitext(uploaded_file)[0])
    print('extensao', file_extension)

    base_folder = "{}/{}".format(OUTPUT_PATH, os.path.splitext(uploaded_file)[0])

    json_path = os.path.join(base_folder, 'summary.json')

    print('jsonpath', json_path)
    print(json_path)
    if not os.path.exists(json_path):
        return jsonify({"error": "summary.json not found in the zip file"}), 400

    with open(json_path, 'r') as json_file:
        data = json.load(json_file)
        proteins = data.get('proteins', [])

        if filtered_ids_list:
            proteins = [protein for protein in proteins if protein['id'] in filtered_ids_list]

        proteins_final = []
        for protein in proteins:
            # Adiciona o campo 'description' com o resultado da chamada do método
            protein['header'] = extract_name(protein.get('header', ''))
            protein['local_alignment_hit'] = extract_name(protein.get('local_alignment_hit', ''))
            protein['tm'] = 0 if (protein.get('tm') == '-' or protein.get('tm') is None) else int(protein.get('tm'))
            proteins_final.append(protein.values())

    header = [
        "ID", "Header", "Sequence", "Subcellular Localization", "TMHMM", "Phobius TM",
        "SignalP5", "Phobius SP", "Local Alignment Hit", "GPI-Anchored",
        "GPI-Anchored Evidence", "Membrane", "Membrane Evidence", "Membrane Evidence Complete",
        "N-Glyc", "Erret", "PFAM", "PANTHER", "InterPro ID", "InterPro Description",
        "GO ID", "GO Description"
    ]
    df = pd.DataFrame(proteins_final, columns=header);
    print(df)
    content_type = "text/plain"
    output = StringIO()
    output_excel = BytesIO()

    if file_extension == 'csv':
        df.to_csv(output, index=False)
        content_type = "text/csv"
    elif file_extension == 'tsv':
        df.to_csv(output, index=False, sep='\t')
        content_type = "text/tsv"
    elif file_extension == 'fasta':
        fasta = ""
        for index, row in df.iterrows():
            fasta += f">{row['ID']} {row['Header']}\n"
            fasta += format_sequence(row['Sequence'])
            fasta += "\n"
        print(fasta)
        output.write(fasta)

    output.seek(0)

    response = make_response(output.read())
    response.headers['Content-Type'] = content_type
    response.headers['Content-Disposition'] = 'attachment; filename=proteins.' + file_extension

    return response


def format_sequence(sequence, width=80):
    return textwrap.fill(sequence, width=width)


app.secret_key = os.urandom(24)  # Chave secreta para criptografar a sessão
app.permanent_session_lifetime = timedelta(hours=1)  # Duração da sessão


@app.before_request
def require_login():
    return
    print('Tentando acesso:', request.endpoint)
    # Permite acesso à página de login e aos arquivos estáticos
    if request.endpoint in ['login', 'static', 'processes']:
        return
    if not session.get('logged_in') and request.endpoint != 'login':
        return redirect(url_for('login'))


def get_logged_user():
    if session['logged_in']:
        return session['user']


def auto_login():
    if FLASK_DEBUG:
        default_user = {
            'user': 'dev',
            'password': base64.b64encode('dev'.encode()).decode(),
            'name': 'Administrator Dev - Debug',
            'role': 'ADMIN'
        }

        session.permanent = True
        session['logged_in'] = True
        session['user'] = default_user


@app.route('/login', methods=['GET', 'POST'])
def login():
    session['type'] = 'login'
    if request.method == 'POST':
        username = request.form.get('username')
        password = request.form.get('password')

        users = load_users()

        if username in users:

            encoded_password = base64.b64encode(password.encode('utf-8')).decode('utf-8')

            if users[username]['password'] == encoded_password:
                session.permanent = True  # Sessão permanente até expirar
                session['logged_in'] = True
                session['user'] = users[username]
                session['type'] = 'new'
                return render_template('index.html', session=session)
            else:
                flash('Incorrect username or password. Please try again.', 'error')
        else:
            flash('Incorrect username or password. Please try again.', 'error')

    return render_template('login.html')


@app.route('/logout')
def logout():
    session.clear()
    flash('You have been logged out.', 'success')
    return redirect(url_for('login'))


# Users module
USERS_FILE = 'users.json'


def load_users():
    if not os.path.exists(USERS_FILE):
        # Criar arquivo com usuário padrão
        default_user = {
            "admin": {
                "user": "admin",
                "password": base64.b64encode("admin".encode()).decode(),
                "name": "administrator",
                "role": "ADMIN"
            }
        }
        save_users(default_user)
    with open(USERS_FILE, 'r') as file:
        return json.load(file)


def save_users(users):
    with open(USERS_FILE, 'w') as file:
        json.dump(users, file, indent=4)


def admin_required(f):
    @wraps(f)
    def decorated_function(*args, **kwargs):
        user_role = session.get('user', {}).get('role', None)

        if 'user' not in session or user_role != 'ADMIN':
            flash('Access denied. Admins only.', 'error')
            return redirect(url_for('login'))  # Redirecionar para a página de login ou outra página
        return f(*args, **kwargs)

    return decorated_function


@app.route('/users', methods=['GET', 'POST'])
@admin_required
def users():
    session['type'] = 'users'
    users = load_users()

    if request.method == 'POST':
        user = request.form['user']
        password = request.form['password']
        password_confirmation = request.form['password_confirmation']
        name = request.form['name']
        role = request.form['role']

        if user in users:
            flash("User already exists.", "error")
            return render_template('users.html', users=users)

        if password != password_confirmation:
            flash("The passwords do not match.", "error")
            return render_template('users.html', users=users)

        encoded_password = base64.b64encode(password.encode()).decode()
        users[user] = {
            "user": user,
            "password": encoded_password,
            "name": name,
            "role": role
        }
        save_users(users)
        flash("User registered successfully.", "success")
        return render_template('users.html', users=users, session=session)

    return render_template('users.html', users=users)


@app.route('/delete/<user>', methods=['POST'])
def delete_user(user):
    session['type'] = 'users'
    users = load_users()
    if user in users:
        del users[user]
        save_users(users)
        flash("User deleted successfully.", "success")
    return redirect(url_for('users'))


@app.route('/edit/<user>', methods=['POST'])
def edit_user(user):
    session['type'] = 'users'
    users = load_users()
    name = request.form['name']
    role = request.form['role']
    new_password = request.form.get('new_password')
    password_confirmation = request.form.get('new_password_confirmation')

    if new_password and new_password != password_confirmation:
        flash("The passwords do not match.", "error")
        return render_template('users.html', users=users, session=session)

    if user in users:
        users[user]['name'] = name
        users[user]['role'] = role
        if new_password:
            encoded_password = base64.b64encode(new_password.encode()).decode()
            users[user]['password'] = encoded_password
        save_users(users)
        flash("User updated successfully!", "success")

    return redirect(url_for('users'))


@app.route('/about', methods=['GET'])
def about():
    session['type'] = 'about'
    auto_login()
    return render_template('about.html', session=session)


@app.route('/profile', methods=['GET', 'POST'])
def profile():
    auto_login()
    session['type'] = 'profile'
    users = load_users()
    current_user = get_logged_user()
    print(current_user)
    print(users)

    if request.method == 'POST':

        name = request.form.get('name')
        password = request.form.get('password')
        confirm_password = request.form.get('confirm_password')

        encoded_password = base64.b64encode(password.encode()).decode()
        user = current_user['user']
        update = False
        if name != users[user]['name']:
            users[user]['name'] = name
            flash('Name updated', 'success')
            update = True
        if password and confirm_password and password == confirm_password:
            users[user]['password'] = encoded_password
            flash('Password updated', 'success')
            update = True

        if update:
            save_users(users)
            flash('Profile updated successfully.', 'success')
        else:
            flash('No changes performed.', 'error')
    user_data = current_user
    return render_template('profile.html', user=user_data, username=current_user['user'])


@app.route('/dbs', methods=['GET', 'POST'])
def dbs():
    session['type'] = 'dbs'
    if request.method == 'POST':
        if 'file' not in request.files:
            flash('No file provided.', 'error')
            return render_template('dbs.html', session=session)
        file = request.files['file']
        name = request.form.get('name')
        try:
            response = add_db_file(file, name)
            flash('Database created successfuly.', "success")
        except FileProcessingError as e:
            flash(f"{e}", "error")
            return render_template('dbs.html', files=load_db_list(), session=session)
    # List .dmnd files in DB_PATH

    return render_template('dbs.html', files=load_db_list(), session=session)


def convert_size(size_bytes):
    if size_bytes == 0:
        return "0B"
    size_name = ("B", "KB", "MB", "GB", "TB", "PB", "EB", "ZB", "YB")
    i = int(os.floor(os.log2(size_bytes) / 10))
    p = os.pow(1024, i)
    s = round(size_bytes / p, 2)
    return f"{s} {size_name[i]}"


class FileProcessingError(Exception):
    pass


def add_db_file(file, dbname):
    filename = secure_filename(file.filename)
    if ' ' in filename:
        raise FileProcessingError('Filename cannot contain spaces.')

    file_ext = os.path.splitext(filename)[1].lower()
    if file_ext not in ALLOWED_EXTENSIONS:
        raise FileProcessingError('Invalid file format. Only .fa, .fas, .fasta, and .dmnd are accepted.')

    file_path = os.path.join(DB_PATH, filename)
    base_filename = os.path.splitext(filename)[0]
    dmnd_file_path = os.path.join(DB_PATH, dbname)

    if os.path.exists(dmnd_file_path):
        raise FileProcessingError('A database with the same name already exists.')

    file.save(file_path)

    if not is_fasta_file(file_path):
        os.remove(file_path)
        raise FileProcessingError('The file does not contain valid protein FASTA data.')
    try:
        result = subprocess.run(
            ['diamond', 'makedb', '--in', file_path, '-d', dmnd_file_path],
            check=True,
            stdout=subprocess.PIPE,
            stderr=subprocess.PIPE,
            text=True
        )
        print(result)
        print("Output:", result.stdout)
        print("Error Output:", result.stderr)
        os.remove(file_path)
        return 'Database created successfuly.'
    except subprocess.CalledProcessError as e:
            os.remove(file_path)
            print("Error Output:", result.stderr)
            print("Warning: Command returned 1, but the process will continue.")
            raise

# Route for deleting files
@app.route('/delete_db/<filename>', methods=['POST'])
def delete_db(filename):
    session['type'] = 'dbs'
    file_path = os.path.join(DB_PATH, filename)
    if os.path.exists(file_path):
        os.remove(file_path)
        flash('File deleted successfully.', 'success')
    else:
        flash('File not found.', 'error')
    return redirect(url_for('dbs'))


if __name__ == '__main__':
    app.run(debug=True)
