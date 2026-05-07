const http = require('http')
const fs = require('fs')
const path = require('path')

const port = process.env.PORT || 3000
const dist = path.join(__dirname, 'dist')

const MIME = {
  '.html': 'text/html; charset=utf-8',
  '.js': 'application/javascript; charset=utf-8',
  '.css': 'text/css; charset=utf-8',
  '.json': 'application/json',
  '.png': 'image/png',
  '.jpg': 'image/jpeg',
  '.svg': 'image/svg+xml',
  '.ico': 'image/x-icon'
}

http.createServer((req, res) => {
  let url = req.url.split('?')[0]
  if (url === '/') url = '/index.html'
  const filePath = path.join(dist, url.replace(/^\//, ''))
  fs.readFile(filePath, (err, data) => {
    if (err) {
      // SPA fallback: serve index.html for all non-file routes
      fs.readFile(path.join(dist, 'index.html'), (_, html) => {
        res.writeHead(200, { 'Content-Type': 'text/html; charset=utf-8' })
        res.end(html)
      })
    } else {
      const ext = path.extname(filePath)
      res.writeHead(200, { 'Content-Type': MIME[ext] || 'application/octet-stream' })
      res.end(data)
    }
  })
}).listen(port, '0.0.0.0', () => {
  console.log(`Server running on port ${port}`)
})
