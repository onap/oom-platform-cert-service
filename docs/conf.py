from docs_conf.conf import *

branch = 'latest'
master_doc = 'index'

linkcheck_ignore = [
    'http://localhost',
    'http://ejbca',
    'https://localhost'
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

linkcheck_ignore = [r'https://download.primekey.com/docs/EJBCA-Enterprise/6_14_0/CMP.html']
