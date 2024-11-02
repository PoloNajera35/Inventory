# Generated by Django 5.1.2 on 2024-10-21 17:21

import django.db.models.deletion
from django.db import migrations, models


class Migration(migrations.Migration):

    initial = True

    dependencies = [
        ('products', '0001_initial'),
    ]

    operations = [
        migrations.CreateModel(
            name='Disease',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=50, verbose_name='name')),
            ],
            options={
                'verbose_name': 'Disease',
                'verbose_name_plural': 'Disease',
            },
        ),
        migrations.CreateModel(
            name='Client',
            fields=[
                ('id', models.BigAutoField(auto_created=True, primary_key=True, serialize=False, verbose_name='ID')),
                ('name', models.CharField(max_length=100, verbose_name='name client')),
                ('phone', models.BigIntegerField(verbose_name='phone number')),
                ('birthdate', models.DateField(verbose_name='birthdate')),
                ('medicines', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='products.product')),
                ('diseases', models.ForeignKey(on_delete=django.db.models.deletion.CASCADE, to='clients.disease')),
            ],
            options={
                'verbose_name': 'Client',
                'verbose_name_plural': 'Clients',
            },
        ),
    ]