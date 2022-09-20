from docs_conf.conf import *

branch = 'latest'
master_doc = 'index'

linkcheck_ignore = [
    'http://localhost',
    'http://ejbca',
    'https://localhost',
    'https://download.primekey.com'
]

exclude_patterns = [
    '.tox'
]

extensions = [
    'sphinxcontrib.openapi',
]
intersphinx_mapping = {}

html_last_updated_fmt = '%d-%b-%y %H:%M'


def setup(app):
    app.add_css_file("css/ribbon.css")