import subprocess
import time
import os
import os.path
import time
import zipfile
import sys
from flask import Flask, request, render_template, send_from_directory, redirect, url_for, Blueprint
from datetime import datetime
from Bio import SeqIO

app = Flask(__name__)
app.static_url_path='/static'

remove_result_dir = False
remove_result_dir = os.getenv('FLASK_REMOVE_RESULT_DIR')
flask_home = os.getenv('FLASK_HOME')
output_folder = os.getenv('FASTPROTEIN_WEB_OUTPUT')
errors = []

if flask_home is None:
    print('Please, set the environment variable FLASK_HOME to continue')
    sys.exit(0)

app.static_folder=flask_home+'/static'

static_bp = Blueprint('runs', __name__,
                  static_folder=output_folder,
                  static_url_path='/runs')

app.register_blueprint(static_bp, url_prefix='/')
if __name__ == '__main__':
    app.run(port=5000)

@app.route('/static/<path:filename>')
def serve_static(filename):
    return send_from_directory(app.static_folder, filename)

@app.route('/runs/<path:filename>')
def serve_runs(filename):
    return send_from_directory(static_bp.static_folder, filename)


@app.route('/')
def index():
    # get the list of files in the downloads folder
    files = os.listdir(output_folder)

    # create a list of dictionaries representing the files
    file_list = []
    for file_name in files:
        if file_name.endswith(".zip"):
            file_path = os.path.join(output_folder, file_name)
            file_stat = os.stat(file_path)
            file_dict = {
                'name': file_name,
                'seconds': file_stat.st_mtime,
                'date': datetime.fromtimestamp(file_stat.st_mtime).strftime("%A, %B %d, %Y %I:%M:%S"),
                'path': file_name,
            }
            file_list.append(file_dict)

    file_list.sort(key=lambda x: x['seconds'], reverse=True)
    # render the template with the file list

    return render_template('index.html', files=file_list)

@app.route('/upload', methods=['POST'])
def upload():
    name_run = request.form.get('run-name')
    if name_run is None:
        name_run = 'fastprotein'

    timestamp = str(int(time.time()))
    run_id = name_run +'-'+ timestamp

    params = []

    file = request.files['fasta']

    if file.filename != '' :
        inputFasta = output_folder+'/'+run_id+'.fasta'
        file.save(inputFasta)
        print('File saved: ' + inputFasta)
        if is_fasta_file(inputFasta):
            params.extend(['-i', inputFasta])
        else:
            error ='Input file is not a FASTA file. Please check the file and try again.'
            os.remove(inputFasta)
            print('Error: ' + error)
            return error
    else:
        return 'Error: Inform a FASTA file'

    subcell = request.form.get('subcell')
    if subcell is None:
        print("subcell=no")
    else:
        print("subcell=" + subcell)
        params.extend(['-s', subcell])

    remote_service = False;
    interpro = request.form.get('interpro')
    if interpro is None:
        print("interpro=no")
    else:
        remote_service = True;
        print("interpro=yes")
        params.extend(['--interpro'])

    fileBlast = request.files['fastaBlast']
    local_blast = False
    if fileBlast.filename != '':
        inputBlast = output_folder+'/'+run_id+'-blastdb.fasta'
        fileBlast.save(inputBlast)
        if is_fasta_file(inputBlast):
            print('File saved: ' + inputBlast)
            print("local blast=true")
            params.extend(['--local-blast', inputBlast])
            local_blast = True
        else:
            error = 'Local Blast DB is not a FASTA file. Please check the file and try again.'
            print('Error: ' + error)
            os.remove(inputBlast)
            return error

    params.extend(['-zip'])


    folder = output_folder+"/" + run_id+'_results'
    params.extend(['-o', folder])

    subprocess.run(['fastprotein'] + params)


    print('Result files: ' +  folder +".zip")
    print('Removing input files')
    subprocess.run(['rm', inputFasta])
    if local_blast:
        subprocess.run(['rm', inputBlast])
    subprocess.run(['rm', '-r', folder])

    return redirect(url_for('index'))

@app.route('/remove_file', methods=['POST'])
def remove_file():
   filename = request.form['filename']
   print ('Removendo o arquivo ' + filename)
   file = '/fastprotein/runs/'+filename
   if os.path.exists(file):
        os.remove(file)
   return redirect(url_for('index'))

def is_fasta_file(filepath):
    try:
        # try parsing the file using SeqIO.parse()
        records = SeqIO.parse(filepath, 'fasta')
        print(records)
        count = 0
        # loop through the records in the iterator
        for record in records:
            count=count+1
        print(f"Proteins in FASTA {count}")

        print(filepath + ' is a valid FASTA')
        # if parsing succeeds, the file is in FASTA format
        return True
    except ValueError:
        print(filepath + ' is a invalid FASTA')
        # if parsing fails, the file is not in FASTA format
        return False
