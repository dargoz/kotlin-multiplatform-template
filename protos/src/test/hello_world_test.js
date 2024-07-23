import { Client, StatusOK } from 'k6/net/grpc';
import { check, sleep } from 'k6';

const client = new Client();
client.load(['../main/proto/io/grpc/examples/helloworld'], 'hello_world.proto');

export default () => {
  // use plaintext : true for http server (usually development localhost without TLS)
  client.connect('127.0.0.1:50051', { plaintext: true });

  const data = { name: 'davin' };
  const response = client.invoke('helloworld.Greeter/SayHello', data);

  check(response, {
    'status is OK': (r) => r && r.status === StatusOK,
  });

  console.log(JSON.stringify(response.message));

  client.close();
  sleep(1);
};