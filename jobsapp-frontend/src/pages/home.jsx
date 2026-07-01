import { Link } from 'react-router-dom'

export default function Home() {
  return (
    <main role="main" className="main-content">
      <div className="container">
        <div className="p-5 mb-4 bg-light rounded-3">
          <div className="container-fluid py-5">
            <h1 className="display-5 fw-bold">Welcome to JobsHub</h1>
            <p className="col-md-8 fs-4">
              This is a simple home page. Use the menu to go to the categories view.
            </p>
            <Link to="/categories" className="btn btn-primary btn-lg">
              Go to Categories
            </Link>
          </div>
        </div>
      </div>
    </main>
  )
}