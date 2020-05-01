from flask import Flask, render_template, request, flash
import rocksdb

app = Flask(__name__)

@app.route('/')
def hello_world():
    return render_template('query_code.html', results=set({}))

@app.route('/search', methods=['GET'])
def search_query():
    queries = request.args.get('inputlabel')
    urls  = set({})
    db1 = rocksdb.DB("search4.db", rocksdb.Options(create_if_missing=False))

    if queries is not None:
        query = queries.lower().split()

        res = db1.multi_get([bytes(query1, 'utf-8') for query1 in query]).values()
        print(res)
        
        if not any(res):
            return render_template('query_code.html', results=["No results found"])
        
        for i in res:
            if i is not None:
                for j in str(i)[2:-1].split(','):
                    urls.add(j)
            
    return render_template('query_code.html', results=list(urls))


if __name__ == '__main__':
   app.run()
   # db1.close()