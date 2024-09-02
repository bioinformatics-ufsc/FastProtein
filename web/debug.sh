export FLASK_RUN_HOST=0.0.0.0
export FLASK_DEBUG=True
export FLASK_HOME='/FastProtein/web'
export FLASK_APP=$FLASK_HOME""'/server.py'
export FLASK_REMOVE_RESULT_DIR=Yes

flask run
