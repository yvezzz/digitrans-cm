import http from 'k6/http';
import { check, sleep, group } from 'k6';
import { Rate, Trend } from 'k6/metrics';

const BASE_URL = __ENV.BASE_URL || 'http://localhost:8080/api/v1';

const errorRate = new Rate('errors');
const responseTime = new Trend('response_time');

export const options = {
  stages: [
    { duration: '2m', target: 20 },
    { duration: '3m', target: 50 },
    { duration: '2m', target: 100 },
    { duration: '2m', target: 50 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_duration: ['p(95)<2000'],
    errors: ['rate<0.1'],
  },
};

function getToken() {
  // TODO: remplacer par un vrai token Cognito
  return 'eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.placeholder';
}

export default function () {
  const token = getToken();
  const params = {
    headers: {
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json',
    },
  };

  group('ERP - Employees', function () {
    let res = http.get(`${BASE_URL}/erp/employees`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    responseTime.add(res.timings.duration);
    sleep(1);
  });

  group('ERP - Suppliers', function () {
    let res = http.get(`${BASE_URL}/erp/suppliers`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('ERP - Invoices', function () {
    let res = http.get(`${BASE_URL}/erp/invoices`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('CRM - Clients', function () {
    let res = http.get(`${BASE_URL}/crm/clients`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('CRM - Restaurants', function () {
    let res = http.get(`${BASE_URL}/crm/restaurants`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('Supply Chain - Products', function () {
    let res = http.get(`${BASE_URL}/supply/products`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('Supply Chain - Shipments', function () {
    let res = http.get(`${BASE_URL}/supply/shipments`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });

  group('BI - Dashboard', function () {
    let res = http.get(`${BASE_URL}/bi/dashboard`, params);
    check(res, { 'status 200': (r) => r.status === 200 });
    errorRate.add(res.status !== 200);
    sleep(1);
  });
}
